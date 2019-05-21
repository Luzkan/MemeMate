package com.codecrew.mememate.activity.top

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.profile.GalleryMemeClickListener
import com.codecrew.mememate.database.models.MemeModel
import com.squareup.picasso.Picasso

class TopAdapter(private val memes: List<MemeModel>) : RecyclerView.Adapter<TopViewHolder>() {

    private var context: Context? = null
    var listener: GalleryMemeClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_top, parent,
            false
        )
        return TopViewHolder(view)
    }

    override fun getItemCount(): Int {
        return memes.size
    }

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        if (position < itemCount) {
            val meme = memes[position]
            Picasso.get()
                .load(meme.url)
                .into(holder.memeImageView)
        }

        val index = (position + 1).toString() + "."

        val currentMeme = memes[position]

        holder.index.text = index
        holder.rate.text = currentMeme.rate.toString()
        holder.location.text = currentMeme.location
        holder.username.text = currentMeme.addedBy
        holder.itemView.setOnClickListener {
            listener?.onGalleryMemeClickListener(holder.adapterPosition)
        }
    }
}