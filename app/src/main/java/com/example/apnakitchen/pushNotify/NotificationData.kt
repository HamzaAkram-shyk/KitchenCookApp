package com.example.apnakitchen.pushNotify

import com.example.apnakitchen.Utils.Default

data class NotificationData(
    val title: String,
    val message: String,
    val senderType: String,
    val notificationType:String = Default
)
