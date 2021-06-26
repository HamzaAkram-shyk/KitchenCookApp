package com.example.apnakitchen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apnakitchen.R
import com.example.apnakitchen.model.chatmodel.Message
import com.example.apnakitchen.model.chatmodel.SenderType
import kotlinx.android.synthetic.main.cook_chat_layout.view.*
import kotlinx.android.synthetic.main.cook_profile.*
import kotlinx.android.synthetic.main.customer_chat_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatAdapter(
    private val context: Context,
    private var messages: ArrayList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageView = itemView.customer_message_label!!
        val time = itemView.customerTimeField!!
    }

    private inner class CookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageView = itemView.cook_message_label!!
        val time = itemView.cookTimeField!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.cook_chat_layout -> {
                CookViewHolder(itemView)
            }
            R.layout.customer_chat_layout -> {
                CustomerViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")

        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (message.senderType) {
            SenderType.COOK -> {
                (holder as CookViewHolder).messageView.text = message.messageBody
                holder.time.text = message.getTime()
            }

            SenderType.CUSTOMER -> {
                (holder as CustomerViewHolder).messageView.text = message.messageBody
                holder.time.text = message.getTime()
            }

        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val senderType = messages[position].senderType
        return if (senderType == SenderType.COOK) R.layout.cook_chat_layout else R.layout.customer_chat_layout
    }

    fun refreshList(messages: ArrayList<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    private fun Message.getTime(): String {
        val date = Date(this.timeStamp * 1000)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

}