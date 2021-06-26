package com.example.apnakitchen.pushNotify

import com.example.apnakitchen.`interface`.MyResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Notification {

    fun push(notification: PushNotification, callback: NotifyResponse) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.sendNotification(notification)
                if (response.isSuccessful) {
                    callback.onDeliver("${Gson().toJson(response)}")
                } else {
                    callback.onError(response.errorBody().toString())
                }
            } catch (e: Exception) {
                callback.onError(e.message.toString())
            }

        }


}