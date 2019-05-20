package com.codecrew.mememate.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import com.codecrew.mememate.MemeDiffCallback
import com.codecrew.mememate.MemeStackAdapter
import com.codecrew.mememate.R
import com.codecrew.mememate.database.models.MemeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_browse.*



class BrowseFragment : Fragment(), CardStackListener {

    // (SG) Current user
    private val currentUser  = FirebaseAuth.getInstance().currentUser


    //(SG) Layout elements
    private lateinit var cardStackView : CardStackView
    private lateinit var skipButton : FloatingActionButton
    private lateinit var rewindButton : FloatingActionButton
    private lateinit var likeButton : FloatingActionButton

    // (MJ) Swipe related late inits
    private val drawerLayout by lazy { R.id.drawer_layout }

    private val manager by lazy { CardStackLayoutManager(this.context, this) }
    private val adapter by lazy { MemeStackAdapter(createSpots()) }


    // (MJ) Database late init
    private var memeDatabase : FirebaseFirestore? = null
    private val memeList = ArrayList<MemeModel>()


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // (SG) Firebase init
        memeDatabase =  FirebaseFirestore.getInstance()

        // (SG) Downloading memes
        loadMemes()

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }


    /* SWIPE */
    // (MJ) Everything below is for meme browsing & swiping
    override fun onCardSwiped(direction: Direction) {

        val currentMeme = adapter.getSpots()[manager.topPosition]

        if(direction == Direction.Right){
            currentMeme.rate++
        } else {
            currentMeme.rate--
        }
        updateMemeInfo(currentMeme)

        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    // (SG) Function to update the rate of current meme, seenBy array and liked memes of the user
    private fun updateMemeInfo(currentMeme: MemeModel) {

        currentMeme.seenBy.add(currentUser!!.uid)

        val newMemeParameters = mutableMapOf<String,Any>()
        newMemeParameters["rate"] = currentMeme.rate
        newMemeParameters["seenBy"] =currentMeme.seenBy
        memeDatabase!!.document("/Memes/${currentMeme.dbId}").update(newMemeParameters)
        memeDatabase!!.document("/Users/${currentUser.uid}").update("lickedMemes", FieldValue.arrayUnion(currentMeme.url))
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
            Log.d("MEMESKI", "CLICKED")
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        rewindButton.setOnClickListener {
            Log.d("MEMESKI", "CLICKED")
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        likeButton.setOnClickListener {
            Log.d("MEMESKI", "CLICKED")
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

        var memeArray = ArrayList<MemeModel>()

        Log.d("MEMESKI", "TWORZĘ SPOTY")
        memeList.forEach{
            Log.d("MEMESKI", "SPOT")
            memeArray.add(it)
        }
        return memeArray
    }
    /* SWIPE */

    /* DATABASE FUNCTIONS */
    // (MJ) Load Memes from Database Function
    private fun loadMemes() {
        Log.d("MEMESKI", "POBIERAM MEMESKA 1")
        memeList.clear()
        // (SG) Downloading only memes that user haven't seen yet
        memeDatabase!!.collection("Memes").get().addOnSuccessListener {
            Log.d("MEMESKI", "POBIERAM MEMESKA 2 ")
            // (SG) Casting downloaded memes into objects
            for(meme  in it){
                Log.d("MEMESKI", "POBIERAM MEMESKA 3 ")
                val newMeme = MemeModel(dbId =meme.id ,url =meme["url"].toString(),location = meme["location"].toString(),rate =  meme["rate"].toString().toInt(), seenBy =  meme["seenBy"] as ArrayList<String>)
                if(!newMeme.seenBy.contains(currentUser!!.uid)) {
                    memeList.add(newMeme)
                    Log.d("MEMESKI", "POBIERAM MEMESKA")
                }
            }
            memeList.forEach{
                Log.d("MEMESKI", it.toString())
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