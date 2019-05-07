package com.codecrew.mememate

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
import com.yuyakaido.android.cardstackview.*
import java.util.*

class MainActivity : AppCompatActivity(), CardStackListener {

    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { MemeStackAdapter(createSpots()) }

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
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_add -> {
                textMessage.setText(R.string.add)
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

        // (MJ) Meme Swipe
        setupCardStackView()
        setupButton()
    }

    /* SWIPE */
    // (MJ) Everything below is for meme swipe
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
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
        Log.d("CardStackView", "DZIALAJJJJJJJJJJJJJJJJJJJJJJ ${manager.topPosition}")
        val old = adapter.getSpots()
        val new = old.plus(createSpots())
        val callback = MemeDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    // (MJ) To whoever is going to implement this into database:
    //      This can be easily used with database, check my github repo for "Photer" (https://github.com/Luzkan/Photer)
    //      Should be easy to understand how to make database as it's literally the same thing here
    private fun createSpots(): List<MemeInfo> {
        val meme = ArrayList<MemeInfo>()
        meme.add(MemeInfo(name = "Kamil Guwniks", description = "Notre Dame", url = "https://preview.redd.it/c4onlm6uqss21.jpg?width=960&crop=smart&auto=webp&s=d49bd6c7317ae3c1453c3d5ea7c938c782278acd"))
        meme.add(MemeInfo(name = "Natalka Pralka", description = "Dolar Shave Club", url = "https://i.redd.it/28tx1dduxtw21.jpg"))
        meme.add(MemeInfo(name = "Karol Wielki", description = "Skateboard", url = "https://preview.redd.it/wrm1muwqztw21.jpg?width=960&crop=smart&auto=webp&s=7542fe87acb3b61dbcd5f9d9498349b89dfd566b"))
        meme.add(MemeInfo(name = "Jan Kowalski", description = "Rosja", url = "https://preview.redd.it/pynfxhv4itw21.jpg?width=960&crop=smart&auto=webp&s=3b846767d1ebf6b7e9e5c8d4ee2e10dc9f1617c6"))
        meme.add(MemeInfo(name = "Natalka Pralka", description = "4chan", url = "https://i.redd.it/esj4i04bcsw21.png"))
        meme.add(MemeInfo(name = "Piotr Pietro", description = "Hot Weels", url = "https://preview.redd.it/5ikldesz1tw21.jpg?width=960&crop=smart&auto=webp&s=cf904ad656f6b88957be3b287fa4fdc7a5c01f23"))
        meme.add(MemeInfo(name = "Piotr Skowyrski", description = "Hot Weels", url = "https://preview.redd.it/b86lwcgf2tw21.jpg?width=640&crop=smart&auto=webp&s=f8a7beaa2b04140beabdecd39f7cee94e320b5f5"))

        return meme
    }
    /* SWIPE */



}
