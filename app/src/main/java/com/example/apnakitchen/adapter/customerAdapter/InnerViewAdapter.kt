package com.example.apnakitchen.adapter.customerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import com.example.apnakitchen.model.cookModel.Dish
import kotlinx.android.synthetic.main.dish_grid_item.view.*

class InnerViewAdapter(private val context: Context, private var dishes: List<Dish>) :
    RecyclerView.Adapter<InnerViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishTitle: TextView = itemView.title
        val dishIcon: ImageView = itemView.dish_icon
        val dishPrice: TextView = itemView.price_label
        val dishRating: TextView = itemView.rating_label

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.dish_grid_item, parent, false)
        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        holder.dishTitle.text = dish.name
        holder.dishPrice.text = "Pkr: ${dish.price}"
        holder.dishRating.text = "${dish.rating}"
        Glide.with(context)
            .load(dish.firstImageUrl)
            .into(holder.dishIcon)
    }

    override fun getItemCount(): Int {
        return dishes.size
    }
}