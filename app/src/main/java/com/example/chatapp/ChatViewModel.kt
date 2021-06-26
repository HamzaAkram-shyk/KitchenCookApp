package com.example.chatapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apnakitchen.Utils.Resource
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.chatmodel.Message

class ChatViewModel : ViewModel() {
    var messages = MutableLiveData<Resource<ArrayList<Message>>>()

    fun getChat(chatId: String) {
        messages = ChatRepository.getChat(chatId)
    }

    fun sendMessage(message: Message): LiveData<Resource<Message>> {
        return ChatRepository.sendMessage(message)
    }

    fun getOrder(orderId: String): LiveData<Resource<Order>> {
        return ChatRepository.getOrder(orderId)
    }

}