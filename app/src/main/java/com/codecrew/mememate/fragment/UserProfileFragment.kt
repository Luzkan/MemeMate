package com.codecrew.mememate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity

class UserProfileFragment : ProfileFragment(R.layout.fragment_profile) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = (activity as MainActivity).clickedUserNameID
        if ((activity as MainActivity).clickedUserMemesList != null) {

        }
//        likedMemesList = (activity as MainActivity).clickedUserLikedMemesList
        userMemesList = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val v = super.onCreateView(inflater, container, savedInstanceState)
        loadLikedMemes()
        loadUserMemes()
        (activity as MainActivity).clickedUserLikedMemesList = likedMemesList
        (activity as MainActivity).clickedUserMemesList = userMemesList
        return v
    }
}