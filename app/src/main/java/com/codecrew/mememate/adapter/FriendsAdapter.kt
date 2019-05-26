package com.codecrew.mememate.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.database.models.UserModel
import com.codecrew.mememate.interfaces.UsernameClickListener

class FriendsAdapter(private val friends: ArrayList<UserModel>) : RecyclerView.Adapter<FriendsViewHolder>() {

    private var context: Context? = null
    var userNameListener: UsernameClickListener? = null
//    var listener: GalleryMemeClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_friends, parent,
            false
        )
        return FriendsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {

        val user = friends[position]
        holder.username.text = user.userName
        holder.username.setOnClickListener {
            Log.d("LOLEK","CLICKEDDDD")
            userNameListener?.onUsernameClick(user.uid)
        }
//        holder.username.setOnClickListener{
//            (context as MainActivity).displayProfile(user.uid)
//        }

//        tutaj ustawiamy jak ktoś dodał nowe
//        holder.news.text = "2"

        //ttutaj jak chcemy zeby zniknęło
//        holder.news.visibility = View.INVISIBLE / View.VISIBLE

        // listener podłączyć do profilu użytkownika
//        holder.itemView.setOnClickListener {
//            listener?.onGalleryMemeClickListener(holder.adapterPosition, memes)
//        }
    }
}