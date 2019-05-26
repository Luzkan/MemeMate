package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.fragment_profile_user.view.*

class UserProfileFragment : ProfileFragment(R.layout.fragment_profile_user) {

    var amIFollowing: Boolean = false
    val loggedUser= FirebaseAuth.getInstance().currentUser
    lateinit var followButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = (activity as MainActivity).clickedUserNameID ?: FirebaseAuth.getInstance().currentUser!!.uid

        loggedUser?.also {
            database.document("Users/${loggedUser.uid}")
                .get()
                .addOnSuccessListener {
                    for (userID in it["following"] as ArrayList<String>) {
                        if (userID == this.userID) {
                            amIFollowing = true
                            break
                        }
                    }
                }
        }
        getMemes()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        followButton = v.follow_button
        if (amIFollowing) {
            followButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_unfollow))
        }
        followButton.setOnClickListener{ followUser() }
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

    private fun followUser() {
        if (amIFollowing) {
            loggedUser?.also {
                val loggedUserID = it.uid
                database
                    .document("Users/$userID")
                    .update("followers", FieldValue.arrayRemove(loggedUserID))
                database
                    .document("Users/$loggedUserID")
                    .update("following", FieldValue.arrayRemove(userID))
                Toast.makeText(context, "You are no longer following this user", Toast.LENGTH_SHORT).show()
                followButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_follow))
                amIFollowing = false
            }
        } else {
            loggedUser?.also {
                val loggedUserID = it.uid
                database // Adding logged user as follower of owner of the profile.
                    .document("Users/$userID")
                    .update("followers", FieldValue.arrayUnion(loggedUserID))
                database // Adding owner of the profile to logged user's following list.
                    .document("Users/$loggedUserID")
                    .update("following", FieldValue.arrayUnion(userID))
                Toast.makeText(context, "You are now following this user", Toast.LENGTH_SHORT).show()
                followButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_unfollow))
                amIFollowing = true
            }
        }
    }
}