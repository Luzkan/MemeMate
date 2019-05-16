package com.codecrew.mememate.activity.top

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.codecrew.mememate.R

class TopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var index: TextView = itemView.findViewById(R.id.tvIndex)
    var memeImageView: ImageView = itemView.findViewById(R.id.main_meme)
}