package com.codecrew.mememate

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.codecrew.mememate.database.models.MemeModel

class MemeStackAdapter(private var spots: List<MemeModel> = emptyList()) : RecyclerView.Adapter<MemeStackAdapter.ViewHolder>() {

    var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        Log.d("newMemes","${spots.size}")
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("newMemes","ADAPETER DODAJE!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        // (SG) position of currently displayed meme
        currentPosition = position
        val spot = spots[position]
        holder.name.text = "${spot.id}"
        holder.city.text = spot.location
        Glide.with(holder.image)
            .load(spot.url)
            .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, "???ADAPETER???", Toast.LENGTH_SHORT).show()
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

    fun getPosition() : Int {
        return currentPosition
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}