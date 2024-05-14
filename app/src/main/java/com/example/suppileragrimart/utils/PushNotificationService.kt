package com.example.suppileragrimart.utils

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.suppileragrimart.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Update server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Response to received message

        val title = message.notification?.title
        val body = message.notification?.body
        val dataMap = message.data
        val fullName = dataMap.get(Constants.USER)

        val notificationBuilder = NotificationCompat.Builder(this, "server")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(fullName)
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

}