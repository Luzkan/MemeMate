package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.adapter.FriendsAdapter
import com.codecrew.mememate.database.models.UserModel

class FriendsFragment : Fragment() {

    private lateinit var recyclerViewFriends: RecyclerView

    private var firendsList = ArrayList<UserModel>()
    private lateinit var friendsAdapter: FriendsAdapter
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        firendsList.add(UserModel("","","Shimek",null,null))
        firendsList.add(UserModel("","","Michalec",null,null))
        //todo dodać na początku taki z nazwą You don't have friends
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // (SG) Find widgets
        val v = inflater.inflate(R.layout.fragment_friends, container, false)
        recyclerViewFriends = v.findViewById(R.id.recyclerViewFriends) as RecyclerView

        friendsAdapter = FriendsAdapter(firendsList)
        recyclerViewFriends.layoutManager = LinearLayoutManager(this.context)
        recyclerViewFriends.adapter = friendsAdapter

        return v
    }
}