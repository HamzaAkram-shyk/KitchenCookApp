package com.example.apnakitchen.adapter.customerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apnakitchen.R
import com.example.apnakitchen.adapter.cookAdapter.DishAdapter
import com.example.apnakitchen.model.cookModel.Dish
import com.example.apnakitchen.model.customerModel.ListModel
import kotlinx.android.synthetic.main.nested_layout.view.*

class OuterViewAdapter(private val context: Context, private var list: MutableList<ListModel>) :
    RecyclerView.Adapter<OuterViewAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.nestedView
        val mainTitle: TextView = itemView.main_title

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.nested_layout, parent, false)

        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val adapter = InnerViewAdapter(context, data.list!!)
        holder.recyclerView.hasFixedSize()
        holder.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        holder.mainTitle.text = "${data.title}"
        holder.recyclerView.adapter = adapter

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun refreshList(item: ListModel) {
        list.add(item)
        notifyDataSetChanged()
    }

}