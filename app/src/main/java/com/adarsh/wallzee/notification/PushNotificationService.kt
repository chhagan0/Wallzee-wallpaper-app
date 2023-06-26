package com.adarsh.wallzee.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.adarsh.walzee.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class PushNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotifications(message)
    }

    private fun showNotifications(message: RemoteMessage) {
        val title: String = message.notification?.title.toString()
        val text: String = message.notification?.body.toString()

        val builder = NotificationCompat.Builder(this, "123")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
        createNotificationChannel(text, title)
    }


    private fun createNotificationChannel(text: String, title: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            "123",
            title,
            importance
        ).apply {
            description = text
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}