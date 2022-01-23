package com.example.apnakitchen.adapter.cookAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import com.example.apnakitchen.`interface`.MyResponse
import com.example.apnakitchen.model.cookModel.ExtraMeal
import com.example.apnakitchen.model.cookModel.ExtraMealOrder
import kotlinx.android.synthetic.main.extra_meal_item.view.*
import kotlinx.android.synthetic.main.extra_meal_order.view.*
import kotlinx.android.synthetic.main.listitem_dish.view.*

class ExtraMealOrderAdapter(
    private var extraMeals: ArrayList<ExtraMealOrder>,
    private val context: Context,
) : RecyclerView.Adapter<ExtraMealOrderAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealTitle: TextView = itemView.dishName
        val cookTiming: TextView = itemView.cookTime
        val orderId: TextView = itemView.orderId
        val price: TextView = itemView.payment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.extra_meal_order, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return extraMeals.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = extraMeals[position]
        holder.mealTitle.text = order.meal.name
        holder.price.text = "Pkr: ${order.meal.price}"
        holder.cookTiming.text = "Ready in ${order.meal.CookTime} Hours"
        holder.orderId.text = order.orderId


    }

    fun addList(list: ArrayList<ExtraMealOrder>) {
        extraMeals = list
        notifyDataSetChanged()
    }

    fun appendList(list: List<ExtraMealOrder>) {
        extraMeals.addAll(list)
        notifyDataSetChanged()
    }


}