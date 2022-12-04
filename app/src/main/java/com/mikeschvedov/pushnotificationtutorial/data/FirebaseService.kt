package com.mikeschvedov.pushnotificationtutorial.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mikeschvedov.pushnotificationtutorial.R
import com.mikeschvedov.pushnotificationtutorial.ui.MainActivity
import kotlin.random.Random

private const val CHANNEL_ID = "defaultChannelName"


class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        println("This is the token: $newToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        /*----------------------- Show a notification based on the message -------------------- */
        if (tiramisuPermissionsCheck()) {
            // Create a notification
            val intent = Intent(this, MainActivity::class.java)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }
            // Clears other activities until our MainActivity opens up
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // So when we click the notification we are going to open the main activity
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            // Create the actual notification
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setColor(Color.GREEN)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            // Showing the notification
            notificationManager.notify(notificationID, notification)
        }
    }

    private fun tiramisuPermissionsCheck(): Boolean {
        // If we are above level 33, check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "TutorialNotificationsChannel"
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Our notification channel"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}