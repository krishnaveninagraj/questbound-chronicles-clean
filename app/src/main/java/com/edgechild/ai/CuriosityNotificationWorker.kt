package com.edgechild.ai

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class CuriosityNotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val facts = listOf(
            "Did you know whales sing underwater to talk to their friends across the ocean? 🐋",
            "Did you know honeybees do a special waggle dance to tell other bees where yummy flowers are? 🐝",
            "Did you know astronaut footprints on the moon will stay there for millions of years because there is no wind? 🌙",
            "Did you know sea otters hold hands while sleeping so they don't float away from each other? 🦦"
        )
        val randomFact = facts.random()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "curiosity_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Ask Me Why Curiosity", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("🌟 Ask Me Why Daily Curiosity!")
            .setContentText(randomFact)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomFact))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
        return Result.success()
    }
}
