package com.example.tunebox.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tunebox.MainActivity
import com.example.tunebox.R

class NotificationManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_LOCAL = "tunebox_local"
        const val CHANNEL_ID_PUSH = "tunebox_push"
        const val CHANNEL_ID_MUSIC = "tunebox_music"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal para notificações locais
            val localChannel = NotificationChannel(
                CHANNEL_ID_LOCAL,
                "Notificações Locais",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lembretes e notificações do app"
            }

            // Canal para push
            val pushChannel = NotificationChannel(
                CHANNEL_ID_PUSH,
                "Notificações Push",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações do servidor"
            }

            // Canal para música
            val musicChannel = NotificationChannel(
                CHANNEL_ID_MUSIC,
                "Controle de Música",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controle e atualizações de música"
            }

            notificationManager.createNotificationChannel(localChannel)
            notificationManager.createNotificationChannel(pushChannel)
            notificationManager.createNotificationChannel(musicChannel)
        }
    }

    fun showLocalNotification(
        title: String,
        message: String,
        notificationId: Int = 1
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_LOCAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun showMusicNotification(
        title: String,
        artist: String,
        notificationId: Int = 100
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MUSIC)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText("por $artist")
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun showPushNotification(
        title: String,
        message: String,
        notificationId: Int = 200
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_PUSH)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}
