package com.example.apnakitchen.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import com.example.apnakitchen.Utils.COOK
import com.example.apnakitchen.Utils.CUSTOMER
import com.example.apnakitchen.Utils.Reuse
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.model.Order
import com.example.chatapp.ChatPortal
import kotlinx.android.synthetic.main.current_order_item.view.*

class OrderAdapter(
    private val context: Context,
    private var orders: ArrayList<Order>,
    private val userType: String
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var callback: MyResponse<Order>
    fun setListener(callback: MyResponse<Order>) {
        this.callback = callback
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishTitle: TextView = itemView.dishName
        val dishTime: TextView = itemView.cookTime
        val payment: TextView = itemView.payment
        val orderId: TextView = itemView.orderId
        val chatIcon: ImageView = itemView.chatButton
        val gifAnim: ImageView = itemView.gif
        val orderDeliverBtn: SwitchCompat = itemView.orderCompleteBtn
        val orderStatus: TextView = itemView.orderStatus

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.current_order_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var order = orders[position]
        holder.dishTitle.text = order.dish.name
        holder.dishTime.text = "Order Ready in ${order.dish.CookTime} minutes"
        holder.orderId.text = "OrderId: ${order.timeStamp}"
        holder.payment.text = "Payment: ${order.payment}"
        Glide.with(context)
            .load(R.drawable.cooking)
            .into(holder.gifAnim)

        holder.chatIcon.setOnClickListener {
            val intent = Intent(context, ChatPortal::class.java).let {
                it.putExtra(ChatPortal._key, order)
                context.startActivity(it)
            }

        }
        if (userType == COOK) {
            holder.orderDeliverBtn.visibility = View.VISIBLE
            holder.orderDeliverBtn.isChecked = false
        } else {
            holder.orderStatus.visibility = if (order.deliver) View.VISIBLE else View.GONE
            holder.chatIcon.visibility = if (order.deliver) View.GONE else View.VISIBLE
            holder.dishTime.visibility = if (order.deliver) View.GONE else View.VISIBLE
        }
        holder.orderDeliverBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Reuse.confirmDialog(
                    context,
                    "Order Completed",
                    "Do you want to deliver this order ?"
                ) { check ->

                    if (check) {
                        callback.let {
                            it.success(order, "$position")
                        }
                    } else {
                        holder.orderDeliverBtn.isChecked = false
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun refreshList(list: List<Order>) {
        orders = list as ArrayList<Order>

        notifyDataSetChanged()
    }


}