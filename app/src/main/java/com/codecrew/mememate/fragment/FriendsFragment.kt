package com.codecrew.mememate.fragment

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.transition.TransitionManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.adapter.FeedAdapter
import com.codecrew.mememate.adapter.FriendsAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.codecrew.mememate.interfaces.MemeClickListener
import com.codecrew.mememate.interfaces.UsernameClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_friends.*
import java.util.jar.Manifest


@Suppress("UNCHECKED_CAST")
class FriendsFragment : Fragment(), MemeClickListener, UsernameClickListener  {


    private lateinit var bFriends: Button
    private lateinit var bFeed: Button
    private lateinit var bMessages: Button

    private lateinit var lScreen: LinearLayout
    private lateinit var lFeed: LinearLayout
    private lateinit var lFriends: LinearLayout
    private lateinit var lMessages: LinearLayout

    private lateinit var recyclerViewFriends: RecyclerView
    private var friendsList = ArrayList<UserModel>()
    private var displayList = ArrayList<UserModel>()
    private lateinit var friendsAdapter: FriendsAdapter

    private lateinit var recyclerViewFeed: RecyclerView
    private var feedList = ArrayList<MemeModel>()
    private lateinit var feedAdapter: FeedAdapter

    private lateinit var searchView: SearchView

    private var currentPosition: Int = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var user: UserModel

    override fun onUsernameClick(userID: String) {
        Log.d("LOLEK", "USER ID = $userID")
        (activity as MainActivity).goToClickedUsernameProfile(userID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

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
                downloadFriends()
                downloadFeed()
                //todo
                displayList.addAll(friendsList)
            } else {
                friendsList = (activity as MainActivity).globalFriends!!
                feedList = (activity as MainActivity).globalFeed!!
                downloadFriends()
                downloadFeed()
                //todo
                displayList.clear()
                displayList.addAll(friendsList)
            }
            bFeedClick()
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).globalFriends = friendsList
        (activity as MainActivity).globalFeed = feedList

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // (KS) setting all layouts and buttons
        val v = inflater.inflate(R.layout.fragment_friends, container, false)

        searchView = v.findViewById(R.id.searchView) as SearchView
        searchView.setOnClickListener { searchClick() }
        searching(searchView)

        //todo
        recyclerViewFriends = v.findViewById(R.id.recyclerViewFriends) as RecyclerView
//        friendsAdapter = FriendsAdapter(friendsList)
        friendsAdapter = FriendsAdapter(displayList)
        friendsAdapter.userNameListener = this
        recyclerViewFriends.layoutManager = LinearLayoutManager(this.context)
        recyclerViewFriends.adapter = friendsAdapter

        recyclerViewFeed = v.findViewById(R.id.recyclerViewFeed) as RecyclerView
        if((activity as  MainActivity).globalFeed != null){
            feedAdapter = FeedAdapter((activity as MainActivity).globalFeed!!)
        } else {
            feedAdapter = FeedAdapter(feedList)
        }
        feedAdapter.listener = this
        feedAdapter.userNameListener = this
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

    private fun searchClick() {
        searchView.isIconified = false
    }

    private fun searching(search: SearchView) {

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.isNotEmpty()) {
                    displayList.clear()

                    val searchVal = query.toLowerCase()
                    friendsList.forEach {
                        if (it.userName.toLowerCase().contains(searchVal)) { //todo jak są w bazie z małej to nie trzeba toLowerCase
                            displayList.add(it)
                        }
                    }
                    friendsAdapter.notifyDataSetChanged()
                } else {
                    displayList.clear()
                    displayList.addAll(friendsList)
                    friendsAdapter.notifyDataSetChanged()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    displayList.clear()

                    val searchVal = newText.toLowerCase()
                    friendsList.forEach {
                        if (it.userName.toLowerCase().contains(searchVal)) { //todo jak są w bazie z małej to nie trzeba toLowerCase
                            displayList.add(it)
                        }
                    }
                    friendsAdapter.notifyDataSetChanged()
                } else {
                    displayList.clear()
                    displayList.addAll(friendsList)
                    friendsAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    // (KS) functions to handle changing layouts and buttons
    private fun bFriendsClick() {

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
    override fun onMemeClick(position: Int, memes: ArrayList<MemeModel>) {
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
                if (!friendsList.contains(friend)) {
                    friendsList.add(friend)
                }
            }
        }
        sortFriends(friendsList)
        displayList.clear()
        displayList.addAll(friendsList)
        friendsAdapter.notifyDataSetChanged()
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
                        userID = meme["userId"].toString(),
                        addDate = meme["addDate"].toString()
                    )
                    if (!feedList.contains(memeModel))
                        feedList.add(memeModel)
                }
            }
        }
        sortFeed(feedList)
        feedAdapter.notifyDataSetChanged()
    }

    private fun sortFeed(feedList: ArrayList<MemeModel>) {
        val sortedList = feedList.sortedWith(compareByDescending { it.addDate })
        feedList.clear()
        feedList.addAll(sortedList)
    }

    private fun sortFriends(friendsList: ArrayList<UserModel>) {
        val sortedList = friendsList.sortedWith(compareBy { it.userName })
        friendsList.clear()
        friendsList.addAll(sortedList)
    }

}