package com.example.chatapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.chatmodel.Message
import com.example.apnakitchen.repository.customerRepository.CustomerRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ChatRepository {
    private var chatRef = FirebaseFirestore.getInstance().collection(CHAT_TABLE)
    private var db = FirebaseFirestore.getInstance()

    fun sendMessage(message: Message): MutableLiveData<Resource<Message>> {
        val success = MutableLiveData<Resource<Message>>()
        success.value = Resource.loading(null)
        CoroutineScope(Dispatchers.IO).launch {
            chatRef.document()
                .set(message).addOnSuccessListener {
                    success.value = Resource.success("${message.messageBody}", message)
                }
                .addOnFailureListener {
                    success.value = Resource.error("Sorry Message Not Send!", null)
                }


        }

        return success
    }

    fun getChat(chatId: String): MutableLiveData<Resource<ArrayList<Message>>> {
        val success = MutableLiveData<Resource<ArrayList<Message>>>()
        val messages = ArrayList<Message>()
        success.value = Resource.loading(null)
        chatRef
            .whereEqualTo(CHAT, chatId)
            .orderBy(TimeStamp, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    success.value = Resource.error("there is wrong !!", null)

                    Log.d(TAG, "${error.message.toString()}")
                }
                querySnapshot?.let {
                    messages.clear()
                    for (document in it) {
                        val message = document.toObject(Message::class.java)
                        messages.add(message)
                    }
                    if (messages.size > 0) {
                        success.value = Resource.success("done", messages)
                    } else {
                        success.value = Resource.error("Lets Chat !!", null)
                    }

                }

            }

        return success
    }

    fun getOrder(orderId: String): MutableLiveData<Resource<Order>> {
        val success = MutableLiveData<Resource<Order>>()
        success.value = Resource.loading(null)
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(ORDER_TABLE).document(orderId)
                .get()
                .addOnSuccessListener {
                    val order = it.toObject(Order::class.java)
                    success.value = Resource.success("", order)
                }
                .addOnFailureListener {
                    success.value = Resource.error(it.message.toString(), null)
                }
        }

        return success
    }

}