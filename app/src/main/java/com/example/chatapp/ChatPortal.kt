package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.*
import com.example.apnakitchen.adapter.ChatAdapter
import com.example.apnakitchen.commonModule.detailActivity.DetailActivityViewModel
import com.example.apnakitchen.model.Order
import com.example.apnakitchen.model.chatmodel.Message
import com.example.apnakitchen.model.chatmodel.SenderType
import com.example.apnakitchen.pushNotify.NotificationData
import com.example.apnakitchen.pushNotify.PushNotification
import com.example.apnakitchen.pushNotify.RetrofitInstance
import com.example.apnakitchen.userdashboard.CustomerDashboard
import kotlinx.android.synthetic.main.activity_chat_portal.*
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.coroutines.launch

class ChatPortal : AppCompatActivity() {
    private lateinit var adapter: ChatAdapter
    private lateinit var currentOrder: Order
    private lateinit var senderType: SenderType
    private lateinit var store: DataStoreRepository
    private lateinit var viewModel: ChatViewModel
    private var orderId: String? = null
    private var message: Message? = null


    companion object {
        const val _key = "key"
        const val mainKey = "mainKey"
        var isRunning = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_portal)
        store = DataStoreRepository.getInstance(this)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        intent.let {
            val from = it.getBooleanExtra(mainKey, false)
            if (!from) {
                currentOrder = it.getParcelableExtra<Order>(_key)!!
                viewModel.getChat(currentOrder.orderId!!)
            } else {
                orderId = it.getStringExtra(_key)!!
                initOrder(orderId!!)
            }

        }
        lifecycleScope.launch {
            when (store.getUserType()) {
                COOK -> {
                    senderType = SenderType.COOK
                    titleTextView.text = "Chat With Buyer"
                }
                CUSTOMER -> {
                    senderType = SenderType.CUSTOMER
                    titleTextView.text = "Chat with Cook"
                }
            }

        }
        initialize()

        observeChat()
        send_btn.setOnClickListener {
            sendMessage()
        }


    }

    private fun initOrder(orderId: String) {
        viewModel.getOrder(orderId).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    currentOrder = it.data!!
                    viewModel.getChat(currentOrder.orderId!!)
                    observeChat()
                }
                Status.ERROR -> {
                    Toast.makeText(this, "${it.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initialize() {
        adapter = ChatAdapter(this, ArrayList())
        chatRecyclerView.hasFixedSize()
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter
    }

    private fun sendMessage() {
        if (message_field.text.trim().isNotEmpty()) {
            message = Message(
                currentOrder.orderId,
                Reuse.getTimeStamp(),
                message_field.text.toString(),
                senderType
            )
            message_field.text.clear()
            viewModel.sendMessage(message!!).observe(this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        lifecycleScope.launch {
                            sendNotification(getNotification())
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "Not Send", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Toast.makeText(this, "Please Write some message", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        isRunning = true


    }

    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    private suspend fun sendNotification(notification: PushNotification) {
        try {
            val response = RetrofitInstance.api.sendNotification(notification)
            if (response.isSuccessful) {
                Toast.makeText(this, "Push Notify", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "There is Something Wrong Please Try Again Later",
                    Toast.LENGTH_SHORT
                )
                    .show()
                //  Log.d(com.example.apnakitchen.Utils.TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Exception ${e.message.toString()}", Toast.LENGTH_SHORT).show()
            //  Log.d("Error", e.message.toString())

        }
    }


    private fun getNotification(): PushNotification {
        return PushNotification(
            NotificationData(
                "${currentOrder.orderId}",
                "${message?.messageBody}",
                if (senderType == SenderType.CUSTOMER) CUSTOMER else COOK,
                CHAT
            ),
            if (senderType == SenderType.CUSTOMER) currentOrder.cookToken else currentOrder.buyerToken
        )
    }

    private fun observeChat() {
        viewModel.messages.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    adapter.refreshList(it.data!!)
                    chatRecyclerView.smoothScrollToPosition(it.data!!.size - 1)
                }
                Status.ERROR -> {
                    Toast.makeText(this, "${it.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}