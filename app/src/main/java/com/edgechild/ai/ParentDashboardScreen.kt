package com.edgechild.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ParentDashboardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Read persisted audit logs from Room – survives app restarts / process death.
    // getAllLogs() returns a Flow; collectAsState re-renders the UI when new rows are inserted.
    val dao = remember { AuditLogDatabase.getInstance(context).auditLogDao() }
    val auditLogs by dao.getAllLogs().collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("🛡️ Parent Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                    Text("COPPA & Families Policy Compliance Hub", fontSize = 11.sp, color = Color(0xFF546E7A))
                }
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("← Back to App", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy status card – accurate wording, no false claims
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🔒 Privacy & Safety Information", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• No PII Collection: No names, emails, or raw audio stored by this app.\n" +
                        "• Speech Recognition: Uses Android's on-device STT API; some devices may route audio to Google servers for processing.\n" +
                        "• AI Responses: Sent to Gemini cloud API when an API key is configured; a local fallback is used when unavailable.\n" +
                        "• Ad-Free: Zero third-party tracking or behavioral SDKs.",
                        fontSize = 11.sp,
                        color = Color(0xFF33691E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("📋 Safety Audit Trail & Activity Logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
            Text("Logs persist across app restarts (stored locally on device).", fontSize = 10.sp, color = Color(0xFF78909C))
            Spacer(modifier = Modifier.height(8.dp))

            if (auditLogs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✨ No safety alerts recorded yet! All queries have been safe.",
                            fontSize = 12.sp,
                            color = Color(0xFF78909C)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(auditLogs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "⚠️ Flag: ${log.flagReason}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(log.timestamp, fontSize = 10.sp, color = Color(0xFF90A4AE))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Query: \"${log.userQuery}\"", fontSize = 11.sp, color = Color(0xFF37474F))
                            }
                        }
                    }
                }
            }
        }
    }
}
