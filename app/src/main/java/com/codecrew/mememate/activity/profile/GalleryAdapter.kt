package com.codecrew.mememate.activity.profile

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.database.models.MemeModel
import com.squareup.picasso.Picasso

class GalleryAdapter(private val memes: List<MemeModel>) : RecyclerView.Adapter<MemeViewHolder>() {

    private var context: Context? = null
    var listener: GalleryMemeClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_gallery_meme, parent,
            false
        )
        return MemeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return memes.size
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        if (position < itemCount) {
            val meme = memes[position]
            Picasso.get()
                .load(meme.url)
                .into(holder.memeImageView)
        }

        holder.itemView.setOnClickListener {
            listener?.onGalleryMemeClickListener(holder.adapterPosition)
        }
    }

}