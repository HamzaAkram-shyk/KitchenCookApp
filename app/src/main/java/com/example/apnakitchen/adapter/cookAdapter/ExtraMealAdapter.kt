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
import com.example.apnakitchen.cookdashboard.ui.extrameal_module.ExtraMealList
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.model.cookModel.ExtraMeal
import kotlinx.android.synthetic.main.extra_meal_item.view.*
import kotlinx.android.synthetic.main.listitem_dish.view.*
import kotlinx.android.synthetic.main.listitem_dish.view.dish_image
import kotlinx.android.synthetic.main.listitem_dish.view.dish_price
import kotlinx.android.synthetic.main.listitem_dish.view.dish_timing
import kotlinx.android.synthetic.main.listitem_dish.view.dish_title

class ExtraMealAdapter(
    private var extraMeals: ArrayList<ExtraMeal>,
    private val context: Context,
    private val callback: MyResponse<ExtraMeal>,
    private var isCook: Boolean = true
) : RecyclerView.Adapter<ExtraMealAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealTitle: TextView = itemView.dish_title
        val mealPrice: TextView = itemView.dish_price
        val cookTiming: TextView = itemView.dish_timing
        val instruction: TextView = itemView.instructionField
        val orderBtn: TextView = itemView.orderBtn
        val mealImage: ImageView = itemView.dish_image

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.extra_meal_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return extraMeals.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = extraMeals[position]
        holder.mealTitle.text = meal.name
        holder.mealPrice.text = "Pkr: ${meal.price}"
        holder.cookTiming.text = "Ready in ${meal.CookTime} Hours"
        holder.instruction.text =
            "You will received the orders till ${meal.preOrderTime}:${meal.minute}"
        Glide
            .with(context)
            .load(meal.imageUrl)
            .placeholder(R.drawable.default_loading)
            .into(holder.mealImage)
        if (!isCook) {
            holder.instruction.text =
                "Cooking Start at ${meal.preOrderTime}:${meal.minute}"
            holder.orderBtn.visibility = View.VISIBLE
        }
        holder.orderBtn.setOnClickListener {
            callback.success(meal, "Success")
        }

    }

    fun addList(list: ArrayList<ExtraMeal>) {
        extraMeals = list
        notifyDataSetChanged()
    }

    fun appendList(list: List<ExtraMeal>) {
        extraMeals.addAll(list)
        notifyDataSetChanged()
    }


}