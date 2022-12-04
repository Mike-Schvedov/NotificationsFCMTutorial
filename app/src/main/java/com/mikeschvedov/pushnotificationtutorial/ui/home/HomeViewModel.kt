package com.mikeschvedov.pushnotificationtutorial.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.mikeschvedov.pushnotificationtutorial.interfaces.TopicCallback
import com.mikeschvedov.pushnotificationtutorial.models.PushNotification
import com.mikeschvedov.pushnotificationtutorial.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = Repository()

    fun subscribeToNewTopic(topicInput: String, callback: TopicCallback) {
        FirebaseMessaging.getInstance().subscribeToTopic(topicInput)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.onSubscribed()
                    println("successfully subscribed to the topic")
                } else {
                    println("failed to subscribe to the topic")
                }
            }
            .addOnFailureListener {
                println("failed to subscribe to the topic : ${it.message}")
            }
    }

    fun sendNewMessage(notification: PushNotification) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendNotification(notification)
        }
    }
}