package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.adapter.FeedAdapter
import com.codecrew.mememate.adapter.FriendsAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.codecrew.mememate.interfaces.GalleryMemeClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Suppress("UNCHECKED_CAST")
class FriendsFragment : Fragment(), GalleryMemeClickListener {

    private lateinit var bFriends: Button
    private lateinit var bFeed: Button
    private lateinit var bMessages: Button

    private lateinit var lScreen: LinearLayout
    private lateinit var lFeed: LinearLayout
    private lateinit var lFriends: LinearLayout
    private lateinit var lMessages: LinearLayout

    private lateinit var recyclerViewFriends: RecyclerView
    private var friendsList = ArrayList<UserModel>()
    private lateinit var friendsAdapter: FriendsAdapter

    private lateinit var recyclerViewFeed: RecyclerView
    private var feedList = ArrayList<MemeModel>()
    private lateinit var feedAdapter: FeedAdapter

    private var currentPosition: Int = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var user: UserModel
    private lateinit var memeDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

//        friendsList.add(UserModel("XD","XD", "XD", ArrayList(), ArrayList(), ArrayList(), ArrayList()))
        super.onCreate(savedInstanceState)
        // (SG) Firebase init
        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        db.document("Users/${currentUser!!.uid}").get().addOnSuccessListener {
            user = UserModel(
                uid = it["uid"].toString(),
                email = it["email"].toString(),
                userName = it["username"].toString(),
                likedMemes = it["likedMemes"] as ArrayList<String>?,
                addedMemes = it["addedMemes"] as ArrayList<String>?,
                following = it["following"] as ArrayList<String>?,
                followers = it["followers"] as ArrayList<String>?
            )
            if ((activity as MainActivity).globalFriends == null) {
//                friendsList =  ArrayList()
                downloadFriends()
            } else {
                friendsList = (activity as MainActivity).globalFriends!!
                downloadFriends()
            }
            bFriendsClick()
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).globalFriends = friendsList

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

        return v
    }

    // (KS) functions to handle changing layouts and buttons
    private fun bFriendsClick() {
        Log.d("ONCLICK","B FRIENDS CLICK")

        TransitionManager.beginDelayedTransition(lScreen)

        downloadFriends()

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
        Log.d("ONCLICK","B FEED CLICKED")
        downloadFeed()

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
        Log.d("ONCLICK","B MESSSAGE CLICK")

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

    private fun downloadFriends() {
        user.following!!.forEach { friendID ->
            db.document("Users/$friendID").get().addOnSuccessListener {
                val friend = UserModel(
                    uid = it["uid"].toString(),
                    email = it["email"].toString(),
                    userName = it["userName"].toString(),
                    likedMemes = it["likedMemes"] as ArrayList<String>?,
                    addedMemes = it["addedMemes"] as ArrayList<String>?,
                    following = it["following"] as ArrayList<String>?,
                    followers = it["followers"] as ArrayList<String>?
                )
                friendsList.add(friend)
            }
        }
        friendsList.forEach {
            Log.d("FRIENDS", it.userName)
        }
        Log.d("FRIENDS","FRIENDS LIST SIZE = " + friendsList.size)

        friendsAdapter.notifyDataSetChanged()
        Log.d("FRIENDS","NOTIFYING")
    }

    private fun downloadFeed() {
        friendsList.forEach {
            it.addedMemes!!.forEach { memeID ->
                db.document("Memes/$memeID").get().addOnSuccessListener { meme ->
                    val memeModel = MemeModel(
                        url = meme["url"].toString(),
                        location = meme["location"].toString(),
                        rate = meme["rate"].toString().toInt(),
                        seenBy = meme["seenBy"] as ArrayList<String>,
                        dbId = meme.id,
                        addedBy = meme["addedBy"].toString(),
                        userID = meme["userID"].toString()
                    )
                    feedList.add(memeModel)
                }
            }
        }
        feedAdapter.notifyDataSetChanged()
    }
}