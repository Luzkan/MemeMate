package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.codecrew.mememate.MemeDiffCallback
import com.codecrew.mememate.MemeStackAdapter
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.database.models.MemeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yuyakaido.android.cardstackview.*


class BrowseFragment : Fragment(), CardStackListener {

    // (SG) Current user
    private val currentUser = FirebaseAuth.getInstance().currentUser


    //(SG) Layout elements
    private lateinit var cardStackView: CardStackView
    private lateinit var skipButton: FloatingActionButton
    private lateinit var rewindButton: FloatingActionButton
    private lateinit var likeButton: FloatingActionButton

    // (MJ) Swipe related late inits
    private val drawerLayout by lazy { R.id.drawer_layout }

    private val manager by lazy { CardStackLayoutManager(this.context, this) }
    private val adapter by lazy { MemeStackAdapter(createSpots()) }


    // (MJ) Database late init
    private var memeDatabase: FirebaseFirestore? = null
    private lateinit var  memeList : ArrayList<MemeModel>



    override fun onCreate(savedInstanceState: Bundle?) {
        // (SG) Firebase init
        memeDatabase = FirebaseFirestore.getInstance()

        if( (activity as MainActivity).globalMemeList == null || (activity as MainActivity).globalMemeList!!.size == 0){
            memeList = ArrayList<MemeModel>()
            (activity as MainActivity).globalMemeList = memeList
            // (SG) Downloading memes
            loadMemes(30)
        } else {
            memeList = (activity as MainActivity).globalMemeList!!
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_browse, container, false)
        cardStackView = v.findViewById(R.id.card_stack_view) as CardStackView
        likeButton = v.findViewById(R.id.like_button) as FloatingActionButton
        skipButton = v.findViewById(R.id.skip_button) as FloatingActionButton
        rewindButton = v.findViewById(R.id.rewind_button) as FloatingActionButton

        // (MJ) MemeModel Swipe
        setupCardStackView()
        setupButton()

        return v
    }

    override fun onDestroy() {

        (activity as MainActivity).globalMemeList = memeList
        super.onDestroy()
    }

    /* SWIPE */
    // (MJ) Everything below is for meme browsing & swiping
    override fun onCardSwiped(direction: Direction) {
        memeList.removeAt(0)
        val currentMeme = adapter.getSpots()[manager.topPosition-1]

        if (direction == Direction.Right) {
            currentMeme.rate++
        } else {
            currentMeme.rate--
        }
        updateMemeInfo(currentMeme)

        if(memeList.size < 5){
            loadMemes(30)
        }

        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    // (SG) Function to update the rate of current meme, seenBy array and liked memes of the user
    private fun updateMemeInfo(currentMeme: MemeModel) {

        currentMeme.seenBy.add(currentUser!!.uid)

        val newMemeParameters = mutableMapOf<String, Any>()
        newMemeParameters["rate"] = currentMeme.rate
        newMemeParameters["seenBy"] = currentMeme.seenBy
        memeDatabase!!.document("/Memes/${currentMeme.dbId}").update(newMemeParameters)
        memeDatabase!!.document("/Users/${currentUser.uid}")
            .update("lickedMemes", FieldValue.arrayUnion(currentMeme.url))
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun setupCardStackView() {
        initialize()
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }


    private fun setupButton() {
        skipButton.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        rewindButton.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        likeButton.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    // (MJ) Loads next memes after number that was defined in onCardSwiped
    private fun paginate() {
        val old = adapter.getSpots()
        val new = old.plus(createSpots())
        val callback = MemeDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    // (MJ) Designed for easy db implementation
    private fun createSpots(): List<MemeModel> {

        val memeArray = ArrayList<MemeModel>()

        memeList.forEach {
            memeArray.add(it)
        }
        return memeArray
    }
    /* SWIPE */

    /* DATABASE FUNCTIONS */
    // (MJ) Load Memes from Database Function
    private fun loadMemes(limit : Long) {
        // (SG) Downloading only memes that user haven't seen yet
        memeDatabase!!.collection("Memes").limit(limit).get().addOnSuccessListener {
            // (SG) Casting downloaded memes into objects
            for (meme in it) {
                val newMeme = MemeModel(
                    dbId = meme.id,
                    url = meme["url"].toString(),
                    location = meme["location"].toString(),
                    rate = meme["rate"].toString().toInt(),
                    seenBy = meme["seenBy"] as ArrayList<String>,
                    addedBy = meme["addedBy"].toString()
                )
                if(!newMeme.seenBy.contains(currentUser!!.uid)) {
                    memeList.add(newMeme)
                }

                if(memeList.size < 15){
                    loadMemes(limit + 30)
                }
            }
            reload()
        }.addOnFailureListener {
            Log.d("Error", it.message)
        }
    }

    private fun reload() {
        val old = adapter.getSpots()
        val new = createSpots()
        val callback = MemeDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
    /* DATABASE FUNCTIONS */
}