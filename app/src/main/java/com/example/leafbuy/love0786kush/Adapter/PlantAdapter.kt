package com.example.leafbuy.love0786kush.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.leafbuy.love0786kush.Modal.PlantItem
import com.example.leafbuy.love0786kush.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PlantAdapter(
    options: FirebaseRecyclerOptions<PlantItem>,
    private val context: Context
) : FirebaseRecyclerAdapter<PlantItem, PlantAdapter.PlantViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemcard, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int, model: PlantItem) {
        holder.title.text = model.title ?: "No Title"
        holder.price.text = model.price ?: ""

        Glide.with(context)
            .load(model.imageUrl)
            .into(holder.image)
    }

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val title: TextView = itemView.findViewById(R.id.title)
        val price: TextView = itemView.findViewById(R.id.price)
    }
}
