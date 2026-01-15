package com.example.tunebox.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleLocalNotification(
        title: String,
        message: String,
        delayMinutes: Long = 60
    ) {
        val notificationData = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .build()

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(notificationData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "music_notification",
            ExistingWorkPolicy.KEEP,
            notificationWork
        )
    }

    fun scheduleDailyReminder(
        title: String,
        message: String,
        hourOfDay: Int = 8
    ) {
        val notificationData = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .build()

        val reminderWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            1,
            TimeUnit.DAYS
        )
            .setInitialDelay(calculateInitialDelay(hourOfDay), TimeUnit.MINUTES)
            .setInputData(notificationData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }

    private fun calculateInitialDelay(hourOfDay: Int): Long {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now
            set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
            set(java.util.Calendar.MINUTE, 0)
        }

        if (calendar.timeInMillis <= now) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        return (calendar.timeInMillis - now) / 60000
    }

    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelAllWork()
    }
}
