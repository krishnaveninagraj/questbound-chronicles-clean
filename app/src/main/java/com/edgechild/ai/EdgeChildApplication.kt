package com.edgechild.ai

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class EdgeChildApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialise Room and wire the DAO into SafetyFilter so violations are persisted.
        val db = AuditLogDatabase.getInstance(this)
        SafetyFilter.dao = db.auditLogDao()

        // Schedule daily curiosity notifications via WorkManager.
        // ExistingPeriodicWorkPolicy.KEEP means the schedule survives app restarts
        // without creating duplicate work entries.
        val dailyCuriosity = PeriodicWorkRequestBuilder<CuriosityNotificationWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "curiosity_daily_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyCuriosity
        )
    }
}
