package com.codecrew.mememate.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth

class UserProfileFragment : ProfileFragment(R.layout.fragment_profile) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = (activity as MainActivity).clickedUserNameID ?: FirebaseAuth.getInstance().currentUser!!.uid

        getMemes()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = super.onCreateView(inflater, container, savedInstanceState)

        getMemes()
        return v
    }

    private fun getMemes() {
        if ((activity as MainActivity).clickedUserMemesList == null) {
            userMemesList = ArrayList()
            loadUserMemes()
            (activity as MainActivity).clickedUserMemesList = userMemesList
        } else {
            userMemesList = (activity as MainActivity).clickedUserMemesList!!
        }

        if ((activity as MainActivity).clickedUserLikedMemesList == null) {
            likedMemesList = ArrayList()
            loadLikedMemes()
            (activity as MainActivity).clickedUserLikedMemesList = likedMemesList
        } else {
            likedMemesList = (activity as MainActivity).clickedUserLikedMemesList!!
        }
    }
}