package com.edgechild.ai

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SafetyCheckResult(
    val isSafe: Boolean,
    val flagReason: String,
    val cautionMessage: String
)

/**
 * In-memory + Room-backed audit log store.
 *
 * SafetyGuardrailEngine is the SINGLE point of audit-log writing.
 * VoicePipelineManager must NOT write a second log entry.
 *
 * [dao] is set once from EdgeChildApplication.onCreate() before any user interaction.
 */
object SafetyFilter {
    data class SafetyAuditLog(
        val userQuery: String,
        val flagReason: String,
        val timestamp: String
    )

    /** In-memory list kept for fast same-session reads before Room emits. */
    val auditLogs = mutableListOf<SafetyAuditLog>()

    /** DAO injected at startup – null until EdgeChildApplication.onCreate() runs. */
    var dao: AuditLogDao? = null

    private val dbScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun logViolation(query: String, reason: String) {
        val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        val timeString = timeFormat.format(Date())

        // 1. Update in-memory list (immediate UI feedback for same-session)
        auditLogs.add(0, SafetyAuditLog(query, reason, timeString))

        // 2. Persist to Room so logs survive process death
        dao?.let { auditLogDao ->
            dbScope.launch {
                auditLogDao.insertLog(
                    AuditLogEntity(
                        userQuery = query,
                        flagReason = reason,
                        timestamp = timeString
                    )
                )
            }
        }
    }
}

object SafetyGuardrailEngine {

    private val unsafeKeywords = listOf(
        "suicide", "kill", "harm", "weapon", "gun", "knife", "fire",
        "matches", "lighter", "pill", "medicine", "bleach", "poison",
        "hit", "fight", "punch", "stranger", "run away"
    )

    /**
     * Evaluates [query] for safety.
     * Calls [SafetyFilter.logViolation] on a blocked query – this is the ONLY place
     * a safety audit log entry is created. Do NOT duplicate this call elsewhere.
     */
    fun evaluateQuery(query: String): SafetyCheckResult {
        val lowerQuery = query.lowercase()

        for (keyword in unsafeKeywords) {
            if (lowerQuery.contains(keyword)) {
                val reason = "Unsafe keyword detected: '$keyword'"
                // Single authoritative log write
                SafetyFilter.logViolation(query, reason)
                return SafetyCheckResult(
                    isSafe = false,
                    flagReason = reason,
                    cautionMessage = "⚠️ Let's talk about something else! For safety, please check with a trusted adult or parent before exploring this topic. 🌟"
                )
            }
        }

        return SafetyCheckResult(
            isSafe = true,
            flagReason = "NONE",
            cautionMessage = ""
        )
    }
}
