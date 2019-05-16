package com.codecrew.mememate

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.codecrew.mememate.database.models.MemeModel

class MemeStackAdapter(private var spots: List<MemeModel> = emptyList()) : RecyclerView.Adapter<MemeStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        holder.name.text = "${spot.id}"
        holder.city.text = spot.location
        Glide.with(holder.image)
            .load(spot.url)
            .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, "ADAPTER", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return spots.size
    }

    fun setSpots(spots: List<MemeModel>) {
        this.spots = spots
    }

    fun getSpots(): List<MemeModel> {
        return spots
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}