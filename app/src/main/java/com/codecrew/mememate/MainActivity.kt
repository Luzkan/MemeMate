package com.codecrew.mememate

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import android.support.v4.widget.DrawerLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.codecrew.mememate.database.models.MemeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yuyakaido.android.cardstackview.*
import kotlin.collections.ArrayList

//TODO set browsing meme as default card after login

//TODO potrzebuje dostać aktualnie wyświetlanego mema

class MainActivity : AppCompatActivity(), CardStackListener {


    // (SG) Current user
    private val currentUser  = FirebaseAuth.getInstance().currentUser

    // (MJ) Swipe related late inits
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { MemeStackAdapter(createSpots()) }

    // (MJ) Database late init
    private var memeDatabase : FirebaseFirestore? = null
    private val memeList = ArrayList<MemeModel>()

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_top -> {
                textMessage.setText(R.string.top)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_matches -> {
                textMessage.setText(R.string.matches)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_main -> {
                textMessage.setText(R.string.memes)
                loadMemes()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_add -> {
                textMessage.setText(R.string.add)
                addMeme()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                textMessage.setText(R.string.profile)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // (SG) Fireabase init
        memeDatabase =  FirebaseFirestore.getInstance()

        // (SG) Downloading memes
        loadMemes()

        // (MJ) MemeModel Swipe
        setupCardStackView()
        setupButton()
    }

    /* SWIPE */
    // (MJ) Everything below is for meme browsing & swiping
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

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
        memeDatabase!!.document("/Users/${currentUser.uid}").update("lickedMemes",FieldValue.arrayUnion(currentMeme.url))
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
        val skip = findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
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

        memeList.forEach{
            memeArray.add(it)
        }
        return memeArray
    }
    /* SWIPE */

    /* DATABASE FUNCTIONS */
    // (MJ) Load Memes from Database Function
    private fun loadMemes() {
        memeList.clear()
        // (SG) Downloading only memes that user haven't seen yet
        memeDatabase!!.collection("Memes").get().addOnSuccessListener {
            // (SG) Casting downloaded memes into objects
            for(meme  in it){
                val newMeme = MemeModel(dbId =meme.id ,url =meme["url"].toString(),location = meme["location"].toString(),rate =  meme["rate"].toString().toInt(), seenBy =  meme["seenBy"] as ArrayList<String>)
                if(!newMeme.seenBy.contains(currentUser!!.uid)) {
                    memeList.add(newMeme)
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

    //(PK)Activity for adding new memes
    private fun addMeme(){
        val intent = Intent(this, MemeAdding::class.java)
        startActivity(intent)
    }
}
