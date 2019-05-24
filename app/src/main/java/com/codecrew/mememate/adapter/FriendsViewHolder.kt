package com.codecrew.mememate.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.codecrew.mememate.R

class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var username: TextView = itemView.findViewById(R.id.tvNick)
//    var news: TextView = itemView.findViewById(R.id.tvNew)
}