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
import com.example.apnakitchen.model.cookModel.Dish
import kotlinx.android.synthetic.main.listitem_dish.view.*
import kotlinx.coroutines.CoroutineScope

class DishAdapter(
    private var dishes: ArrayList<Dish>,
    private val context: Context,
    private val callback: MyResponse<Dish>
) : RecyclerView.Adapter<DishAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishTitle: TextView = itemView.dish_title
        val dishPrice: TextView = itemView.dish_price
        val cookTiming: TextView = itemView.dish_timing
        val dishSale: TextView = itemView.dish_sale
        val moreBtn: TextView = itemView.more_btn
        val dishImage: ImageView = itemView.dish_image
        val rating: TextView = itemView.rating_label


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.listitem_dish, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        holder.dishTitle.text = dish.name
        holder.dishPrice.text = "Pkr: ${dish.price}"
        holder.cookTiming.text = "Ready in ${dish.CookTime} minutes"
        holder.rating.text = "${dish.rating}"
        Glide
            .with(context)
            .load(dish.firstImageUrl)
            .placeholder(R.drawable.default_loading)
            .into(holder.dishImage);

        if (dish.sale) {
            holder.dishSale.visibility = View.VISIBLE
            holder.dishSale.text = "${dish.salePercent}% Off"
        } else {
            holder.dishSale.visibility = View.INVISIBLE
        }

        holder.moreBtn.setOnClickListener {
            callback.success(dish, "Success")
        }
    }

    fun addList(list: ArrayList<Dish>) {
        dishes = list
        notifyDataSetChanged()
    }

    fun appendList(list: List<Dish>) {
        dishes.addAll(list)
        notifyDataSetChanged()
    }


}