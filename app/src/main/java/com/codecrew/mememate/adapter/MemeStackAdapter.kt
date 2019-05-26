package com.codecrew.mememate.adapter

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.fragment.GalleryFullscreenFragment
import com.codecrew.mememate.interfaces.UsernameClickListener

class MemeStackAdapter(private var spots: List<MemeModel> = emptyList(), private val context: Context?) :
    RecyclerView.Adapter<MemeStackAdapter.ViewHolder>() {

    var usernameClickListener: UsernameClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(
                R.layout.item_spot,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        holder.name.text = spot.addedBy
        holder.city.text = spot.location
        Glide.with(holder.image)
            .load(spot.url)
            .into(holder.image)
        // (SG) Redirect after username click
        holder.name.setOnClickListener{
            //todo add redirect
        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("images", spots as ArrayList<MemeModel>)
            bundle.putInt("position", position)

            val fragmentTransaction = (context as MainActivity).fragmentManager.beginTransaction()
            val galleryFragment = GalleryFullscreenFragment()
            galleryFragment.arguments = bundle
            galleryFragment.show(fragmentTransaction, "browse")
        }

        holder.name.setOnClickListener { usernameClickListener?.onUsernameClick(spot.userID) }
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

    private fun redirect(username : String){



    }
}