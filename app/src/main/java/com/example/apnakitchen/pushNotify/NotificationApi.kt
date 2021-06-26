package com.example.apnakitchen.pushNotify

import com.example.apnakitchen.pushNotify.Url.CONTENT_TYPE
import com.example.apnakitchen.pushNotify.Url.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun sendNotification(@Body notification: PushNotification): Response<ResponseBody>
}