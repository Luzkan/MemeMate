package com.codecrew.mememate.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.codecrew.mememate.R

class TopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var username: TextView = itemView.findViewById(R.id.item_name)
//    var location: TextView = itemView.findViewById(R.id.item_city)
//    var index: TextView = itemView.findViewById(R.id.tvIndex)
    var memeImageView: ImageView = itemView.findViewById(R.id.main_meme)
    var rate: TextView = itemView.findViewById(R.id.tvRate)
}