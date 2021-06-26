package com.example.apnakitchen.model.chatmodel

import com.example.apnakitchen.Utils.NULL

data class Message(
    var chatId: String = NULL,
    var timeStamp: Long = -1,
    var messageBody: String = NULL,
    var senderType: SenderType = SenderType.DEFAULT
)
