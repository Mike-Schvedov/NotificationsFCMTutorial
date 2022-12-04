package com.mikeschvedov.pushnotificationtutorial.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)