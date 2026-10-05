package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object LunaNotificationHelper {

    const val CHANNEL_CYCLE_ALERTS = "channel_luna_cycle_alerts"
    const val CHANNEL_DAILY_REMINDERS = "channel_luna_daily_reminders"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val cycleChannel = NotificationChannel(
                CHANNEL_CYCLE_ALERTS,
                "Cycle & Period Predictions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for period approaching, fertile window, and cycle updates"
                enableVibration(true)
            }

            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDERS,
                "Daily Logging & Wellness",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle daily prompts to log symptoms, mood, and water intake"
            }

            notificationManager.createNotificationChannel(cycleChannel)
            notificationManager.createNotificationChannel(dailyChannel)
        }
    }

    private fun hasPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun sendPeriodApproachingNotification(context: Context, daysLeft: Int) {
        if (!hasPermission(context)) return
        initChannels(context)

        val title = if (daysLeft <= 1) "Period Predicted Tomorrow" else "Period Predicted in $daysLeft Days"
        val message = "Your next menstrual phase is coming up. Keep warm, stay hydrated, and rest well with Luna."

        val notification = NotificationCompat.Builder(context, CHANNEL_CYCLE_ALERTS)
            .setSmallIcon(R.drawable.mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent(context))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(2001, notification)
        } catch (_: SecurityException) {}
    }

    fun sendFertileWindowNotification(context: Context, isOvulationDay: Boolean) {
        if (!hasPermission(context)) return
        initChannels(context)

        val title = if (isOvulationDay) "Peak Ovulation Day Today 🌸" else "Fertile Window Open ✨"
        val message = if (isOvulationDay) {
            "Highest chance of conception today! Your natural estrogen & energy are at their peak."
        } else {
            "Estimated fertile days have begun. Track your BBT and cervical mucus in Luna."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_CYCLE_ALERTS)
            .setSmallIcon(R.drawable.mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent(context))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(2002, notification)
        } catch (_: SecurityException) {}
    }

    fun sendDailyLogReminder(context: Context) {
        if (!hasPermission(context)) return
        initChannels(context)

        val title = "Gentle Check-in with Luna 🌙"
        val message = "How was your day? Take 30 seconds to log your flow, mood, water intake, and symptoms."

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDERS)
            .setSmallIcon(R.drawable.mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent(context))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(2003, notification)
        } catch (_: SecurityException) {}
    }

    fun sendLatePeriodCheckin(context: Context, daysLate: Int) {
        if (!hasPermission(context)) return
        initChannels(context)

        val title = "Cycle Check-in with Luna 💜"
        val message = "Your period is $daysLate days later than average. Stress, diet, or pregnancy can shift timing. Luna is here to support you."

        val notification = NotificationCompat.Builder(context, CHANNEL_CYCLE_ALERTS)
            .setSmallIcon(R.drawable.mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent(context))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(2004, notification)
        } catch (_: SecurityException) {}
    }

    fun sendTestNotification(context: Context) {
        if (!hasPermission(context)) return
        initChannels(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_CYCLE_ALERTS)
            .setSmallIcon(R.drawable.mascot)
            .setContentTitle("Luna Notifications Active ✨")
            .setContentText("You will receive timely alerts for your period, fertile window, and daily wellness.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent(context))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(2099, notification)
        } catch (_: SecurityException) {}
    }

    fun sendDocumentDownloadedNotification(context: Context, fileName: String, fileUri: android.net.Uri, mimeType: String) {
        if (!hasPermission(context)) return
        initChannels(context)

        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            viewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDERS)
            .setSmallIcon(R.drawable.mascot)
            .setContentTitle("📥 Download Complete: $fileName")
            .setContentText("Medical report saved to Downloads folder. Tap to view.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Your $fileName has been saved to your device Downloads folder. Tap to open and review."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(3001, notification)
        } catch (_: SecurityException) {}
    }
}
