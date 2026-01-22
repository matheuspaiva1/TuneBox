package com.example.tunebox.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val title = inputData.getString("title") ?: "TuneBox"
            val message = inputData.getString("message") ?: ""

            val notificationManager = NotificationManager(applicationContext)
            notificationManager.showLocalNotification(title, message)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
