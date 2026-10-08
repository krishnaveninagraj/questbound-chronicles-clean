package com.edgechild.ai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Request POST_NOTIFICATIONS on Android 13+ for WorkManager daily curiosity notifications
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or denied – notifications are non-critical; no action needed */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+ (non-blocking)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("child_dashboard") }
                    var currentLanguage by remember { mutableStateOf("English") }
                    var showParentalGate by remember { mutableStateOf(false) }

                    when (currentScreen) {
                        "child_dashboard" -> {
                            ChildDashboardScreen(
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it },
                                onOpenParentDashboard = { showParentalGate = true }
                            )
                        }
                        "parent_dashboard" -> {
                            ParentDashboardScreen(
                                onBack = { currentScreen = "child_dashboard" }
                            )
                        }
                    }

                    if (showParentalGate) {
                        ParentalGateDialog(
                            onVerified = {
                                showParentalGate = false
                                currentScreen = "parent_dashboard"
                            },
                            onDismiss = {
                                showParentalGate = false
                            }
                        )
                    }
                }
            }
        }
    }
}
