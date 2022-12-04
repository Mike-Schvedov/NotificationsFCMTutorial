package com.mikeschvedov.pushnotificationtutorial.repository

import com.mikeschvedov.pushnotificationtutorial.models.PushNotification
import com.mikeschvedov.pushnotificationtutorial.network.ApiManager

class Repository {

    private val apiManager: ApiManager = ApiManager()

    suspend fun sendNotification(notification: PushNotification) {
        apiManager.postNotification(notification)
    }

}