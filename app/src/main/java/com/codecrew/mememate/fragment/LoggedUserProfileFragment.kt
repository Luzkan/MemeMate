package com.codecrew.mememate.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.activity.SettingsActivity
import com.google.firebase.auth.FirebaseAuth

class LoggedUserProfileFragment : ProfileFragment(R.layout.fragment_profile) {

    private lateinit var settingsButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = super.onCreateView(inflater, container, savedInstanceState)

        // (PR) Settings button
        settingsButton = v.findViewById(R.id.settings_button)
        settingsButton.setOnClickListener { openSettingsActivity() }

        getMemes()
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userID = FirebaseAuth.getInstance().currentUser!!.uid

        getMemes()
    }

    private fun openSettingsActivity() {
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun getMemes() {
        // (SG) If userMemes Array has not been downloaded yet (When it's first time we click profile tab)
        if ((activity as MainActivity).globalUserMemes == null) {
            userMemesList = ArrayList()
            loadUserMemes()
            (activity as MainActivity).globalUserMemes = userMemesList
        } else {
            userMemesList = (activity as MainActivity).globalUserMemes!!
        }

        // (PR) Same as above but for likedMemes
        if ((activity as MainActivity).globalLikedMemes == null) {
            likedMemesList = ArrayList()
            loadLikedMemes()
            (activity as MainActivity).globalLikedMemes = likedMemesList
        } else {
            likedMemesList = (activity as MainActivity).globalLikedMemes!!
        }
    }
}