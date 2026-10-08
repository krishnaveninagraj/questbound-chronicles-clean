package com.edgechild.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single safety audit log entry.
 * Stored in the "audit_logs" table and survives app restarts / process death.
 */
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userQuery: String,
    val flagReason: String,
    val timestamp: String
)
