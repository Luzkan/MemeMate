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
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.adapter.MemeStackAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.interfaces.MemeDiffCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yuyakaido.android.cardstackview.*


class BrowseFragment : Fragment(), CardStackListener {

    // (SG) Current user
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // (SG) Layout elements
    private lateinit var cardStackView: CardStackView
    private lateinit var skipButton: FloatingActionButton
    private lateinit var likeButton: FloatingActionButton

    private val manager by lazy { CardStackLayoutManager(this.context, this) }
    private val adapter by lazy { MemeStackAdapter(createSpots(), context) }

    // (MJ) Database late init
    private var memeDatabase: FirebaseFirestore? = null
    private lateinit var memeList: ArrayList<MemeModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        // (SG) Firebase init
        memeDatabase = FirebaseFirestore.getInstance()

        if ((activity as MainActivity).globalMemeList == null || (activity as MainActivity).globalMemeList!!.size == 0) {
            memeList = ArrayList()
            (activity as MainActivity).globalMemeList = memeList
            // (SG) Downloading memes
            loadMemes(30)
        } else {
            memeList = (activity as MainActivity).globalMemeList!!
            memeList = removeDuplicatedMemes(memeList)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_browse, container, false)
        cardStackView = v.findViewById(R.id.card_stack_view) as CardStackView
        likeButton = v.findViewById(R.id.like_button) as FloatingActionButton
        skipButton = v.findViewById(R.id.skip_button) as FloatingActionButton

        // (MJ) MemeModel Swipe
        setupCardStackView()
        setupButton()

        return v
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).globalMemeList = memeList

    }

    /* SWIPE */
    // (MJ) Everything below is for meme browsing & swiping
    override fun onCardSwiped(direction: Direction) {

        val currentMeme = adapter.getSpots()[manager.topPosition - 1]

        if (direction == Direction.Right) {
            currentMeme.rate++
            memeDatabase!!.document("/Users/${currentUser!!.uid}")
                .update("likedMemes", FieldValue.arrayUnion(currentMeme.dbId))
        } else {
            currentMeme.rate--
        }
        updateMemeInfo(currentMeme)

        // (SG) When user had seen all memes in database
        if (memeList.size == 0) {

        } else {
            memeList.removeAt(0)
        }

        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    // (SG) Function to update the rate of current meme, seenBy array and liked memes of the user
    private fun updateMemeInfo(currentMeme: MemeModel) {

        currentMeme.seenBy.add(currentUser!!.uid)

        // Force refresh of liked memes in the profile
        (activity as MainActivity).globalLikedMemes = null

        val newMemeParameters = mutableMapOf<String, Any>()
        newMemeParameters["rate"] = currentMeme.rate
        newMemeParameters["seenBy"] = currentMeme.seenBy

        memeDatabase!!.document("/Memes/${currentMeme.dbId}").update(newMemeParameters)
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
    @Suppress("UNCHECKED_CAST")
    private fun loadMemes(limit: Long) {
        Log.d("MEMESKI", "DOWNLOADING")
        Log.d("MEMESKI", "LIMIT = $limit")
        // (SG) Downloading only memes that user haven't seen yet
        memeDatabase!!.collection("Memes").get().addOnSuccessListener {
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
                Log.d("MEMESKI", "Pobrano mema")
                if (!newMeme.seenBy.contains(currentUser!!.uid) && !memeList.contains(newMeme)) {
                    memeList.add(newMeme)
                } else if (memeList.contains(newMeme)) {
                    Log.d("MEMESKI", "ODRZUCONO MEMA")
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

    //(SG) Remove duplicates
    private fun removeDuplicatedMemes(list: ArrayList<MemeModel>): ArrayList<MemeModel> {
        val listWithoutDuplicates = ArrayList<MemeModel>()
        val set = LinkedHashSet<MemeModel>()
        set.addAll(list)
        listWithoutDuplicates.addAll(set)
        return listWithoutDuplicates
    }
}
