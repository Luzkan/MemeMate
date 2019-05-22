package com.codecrew.mememate.activity.profile

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.codecrew.mememate.R

class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var memeImageView: ImageView = itemView.findViewById(R.id.imageView)
}