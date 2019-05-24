package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.adapter.FriendsAdapter
import com.codecrew.mememate.database.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FriendsFragment : Fragment() {

    private lateinit var recyclerViewFriends: RecyclerView

    private var friendsList = ArrayList<UserModel>()
    private lateinit var friendsAdapter: FriendsAdapter
    private var currentPosition: Int = 0
    private lateinit var db : FirebaseFirestore
    private lateinit var currentUser : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {

        db = FirebaseFirestore.getInstance()
        currentUser = (activity as MainActivity).getCurrentUser()
//        friendsList.add(UserModel("","","Shimek",null,null,null,null))
//        friendsList.add(UserModel("","","Michalec",null,null,null,null))

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // (SG) Find widgets
        val v = inflater.inflate(R.layout.fragment_friends, container, false)
        recyclerViewFriends = v.findViewById(R.id.recyclerViewFriends) as RecyclerView

        friendsAdapter = FriendsAdapter(friendsList)
        recyclerViewFriends.layoutManager = LinearLayoutManager(this.context)
        recyclerViewFriends.adapter = friendsAdapter

        if(currentUser.following!!.size == 0){
            //todo dodać na początku taki z nazwą You don't have any friends
        }

        getFollowing()
        return v
    }


    fun getFollowing(){
        friendsList.forEach{
            db.document("Users/$it").get().addOnSuccessListener{
                var friend = UserModel(
                    uid = it["uid"].toString(),
                    email = it["email"].toString(),
                    userName = it["username"].toString(),
                    likedMemes = it["likedMemes"] as ArrayList<String>?,
                    addedMemes = it["addedMemes"] as ArrayList<String>?,
                    following = it["following"] as ArrayList<String>?,
                    followers = it["followers"] as ArrayList<String>?
                )
                friendsList.add(friend)
            }
        }
    }



}