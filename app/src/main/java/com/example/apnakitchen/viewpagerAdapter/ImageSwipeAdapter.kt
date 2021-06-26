package com.example.apnakitchen.viewpagerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apnakitchen.R
import kotlinx.android.synthetic.main.swip_image_layout.view.*

class ImageSwipeAdapter(val images: List<String>) :
    RecyclerView.Adapter<ImageSwipeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.dish_image
        val context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.swip_image_layout, parent, false)

        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        Glide
            .with(holder.context)
            .load(image)
            .placeholder(R.drawable.default_loading)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}