package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import com.codecrew.mememate.R
import com.codecrew.mememate.adapter.FriendsAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.codecrew.mememate.interfaces.GalleryMemeClickListener


class FriendsFragment : Fragment(), GalleryMemeClickListener {

    private lateinit var bFriends: Button
    private lateinit var bFeed: Button
    private lateinit var bMessages: Button

    private lateinit var lScreen: LinearLayout
    private lateinit var lFeed: LinearLayout
    private lateinit var lFriends: LinearLayout
    private lateinit var lMessages: LinearLayout

    private lateinit var recyclerViewFriends: RecyclerView
    private var firendsList = ArrayList<UserModel>()
    private lateinit var friendsAdapter: FriendsAdapter

    private lateinit var recyclerViewFeed: RecyclerView
    private var feedList = ArrayList<MemeModel>()
    private lateinit var feedAdapter: FeedAdapter

    private var currentPosition: Int = 0
    private lateinit var db : FirebaseFirestore
    private lateinit var currentUser : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // (KS) template friends
        friendsList.add(UserModel("", "", "Shimek", null, null))
        friendsList.add(UserModel("", "", "Michalec", null, null))

        // (KS) template feed
        feedList.add(
            MemeModel(
                9999999999,
                "",
                "https://img-9gag-fun.9cache.com/photo/5668845_700bwp.webp",
                "",
                10000,
                ArrayList(),
                "Shimek"
            )
        )
        feedList.add(
            MemeModel(
                9999999998,
                "",
                "https://wyncode.co/uploads/2014/08/81.jpg",
                "",
                2,
                ArrayList(),
                "Michalec"
            )
        )
        feedList.add(
            MemeModel(
                9999999997,
                "",
                "https://pics.me.me/baguette-spaghett-33217703.png",
                "",
                -155,
                ArrayList(),
                "Shimek"
            )
        )

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // (KS) setting all layouts and buttons
        val v = inflater.inflate(R.layout.fragment_friends, container, false)
        recyclerViewFriends = v.findViewById(R.id.recyclerViewFriends) as RecyclerView
        friendsAdapter = FriendsAdapter(friendsList)
        recyclerViewFriends.layoutManager = LinearLayoutManager(this.context)
        recyclerViewFriends.adapter = friendsAdapter

        recyclerViewFeed = v.findViewById(R.id.recyclerViewFeed) as RecyclerView
        feedAdapter = FeedAdapter(feedList)
        feedAdapter.listener = this
        recyclerViewFeed.layoutManager = LinearLayoutManager(this.context)
        recyclerViewFeed.adapter = feedAdapter

        bFriends = v.findViewById(R.id.bFriends) as Button
        bFeed = v.findViewById(R.id.bFeed) as Button
        bMessages = v.findViewById(R.id.bMessages) as Button

        lScreen = v.findViewById(R.id.lFriendsPanel) as LinearLayout
        lFeed = v.findViewById(R.id.lFeed) as LinearLayout
        lFriends = v.findViewById(R.id.lFriends) as LinearLayout
        lMessages = v.findViewById(R.id.lMessages) as LinearLayout

        bFriends.setOnClickListener { bFriendsClick() }
        bFeed.setOnClickListener { bFeedClick() }
        bMessages.setOnClickListener { bMessagesClick() }

        bFeedClick()

        return v
    }

    // (KS) functions to handle changing layouts and buttons
    private fun bFriendsClick() {
        TransitionManager.beginDelayedTransition(lScreen)

        (bFriends.layoutParams as LinearLayout.LayoutParams).weight = 1f
        (bFeed.layoutParams as LinearLayout.LayoutParams).weight = 0f
        (bMessages.layoutParams as LinearLayout.LayoutParams).weight = 0f

        bMessages.text = ""
        bFeed.text = ""
        bFriends.text = getString(R.string.friends)

        lFriends.visibility = View.VISIBLE
        lFeed.visibility = View.GONE
        lMessages.visibility = View.GONE
    }

    private fun bFeedClick() {
        TransitionManager.beginDelayedTransition(lScreen)

        (bFriends.layoutParams as LinearLayout.LayoutParams).weight = 0f
        (bFeed.layoutParams as LinearLayout.LayoutParams).weight = 1f
        (bMessages.layoutParams as LinearLayout.LayoutParams).weight = 0f

        bMessages.text = ""
        bFeed.text = getString(R.string.feed)
        bFriends.text = ""

        lFriends.visibility = View.GONE
        lFeed.visibility = View.VISIBLE
        lMessages.visibility = View.GONE
    }

    private fun bMessagesClick() {
        TransitionManager.beginDelayedTransition(lScreen)

        (bFriends.layoutParams as LinearLayout.LayoutParams).weight = 0f
        (bFeed.layoutParams as LinearLayout.LayoutParams).weight = 0f
        (bMessages.layoutParams as LinearLayout.LayoutParams).weight = 1f

        bMessages.text = getString(R.string.inbox)
        bFeed.text = ""
        bFriends.text = ""

        lFriends.visibility = View.GONE
        lFeed.visibility = View.GONE
        lMessages.visibility = View.VISIBLE
    }

    // (KS) makes fullscreen meme on click
    override fun onGalleryMemeClickListener(position: Int, memes: ArrayList<MemeModel>) {
        currentPosition = position

        val bundle = Bundle()
        bundle.putSerializable("images", memes)
        bundle.putInt("position", position)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "top")
    }
}