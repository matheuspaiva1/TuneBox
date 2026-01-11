package com.example.tunebox.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TuneBoxMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Mensagem recebida de: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title ?: "TuneBox"
        val body = remoteMessage.notification?.body ?: ""

        val notificationManager = NotificationManager(this)
        notificationManager.showPushNotification(title, body)
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Token gerado: $token")
        // Envie este token para seu backend para guardar
        sendTokenToBackend(token)
    }

    private fun sendTokenToBackend(token: String) {
        // Implementar envio do token para seu servidor
        Log.d("FCM", "Token enviado ao backend: $token")
    }
}
