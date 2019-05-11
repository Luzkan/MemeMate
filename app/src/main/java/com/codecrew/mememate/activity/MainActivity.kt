package com.codecrew.mememate.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.EditText
import com.codecrew.mememate.MemeDiffCallback
import com.codecrew.mememate.MemeInfo
import com.codecrew.mememate.MemeStackAdapter
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.profile.ProfileActivity
import com.codecrew.mememate.database.MemeListDatabase
import com.codecrew.mememate.database.models.Meme
import com.yuyakaido.android.cardstackview.*
import java.util.*

class MainActivity : AppCompatActivity(), CardStackListener {

    // (MJ) Swipe related late inits
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { MemeStackAdapter(createSpots()) }

    // (MJ) Database late init
    private var memeDatabase: MemeListDatabase? = null
    private val memeList = ArrayList<MemeInfo>()

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
                temporarySimpleMemeAddition()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                textMessage.setText(R.string.profile)
                startActivity(Intent(this, ProfileActivity::class.java))
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

        // (MJ) Room Database, to be changed into google one later on
        memeDatabase = MemeListDatabase.getInstance(this)

        // (MJ) Meme Swipe
        setupCardStackView()
        setupButton()
    }


    fun openProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
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
        val old = adapter.getSpots()
        val new = old.plus(createSpots())
        val callback = MemeDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    // (MJ) Designed for easy db implementation
    private fun createSpots(): List<MemeInfo> {
        val memes = ArrayList<MemeInfo>()
        memes.add(
            MemeInfo(
                name = "Kamil Guwniks",
                description = "Notre Dame",
                url = "https://preview.redd.it/c4onlm6uqss21.jpg?width=960&crop=smart&auto=webp&s=d49bd6c7317ae3c1453c3d5ea7c938c782278acd"
            )
        )
        memes.add(
            MemeInfo(
                name = "Natalka Pralka",
                description = "Dolar Shave Club",
                url = "https://i.redd.it/28tx1dduxtw21.jpg"
            )
        )
        memes.add(
            MemeInfo(
                name = "Karol Wielki",
                description = "Skateboard",
                url = "https://preview.redd.it/wrm1muwqztw21.jpg?width=960&crop=smart&auto=webp&s=7542fe87acb3b61dbcd5f9d9498349b89dfd566b"
            )
        )
        memes.add(
            MemeInfo(
                name = "Jan Kowalski",
                description = "Rosja",
                url = "https://preview.redd.it/pynfxhv4itw21.jpg?width=960&crop=smart&auto=webp&s=3b846767d1ebf6b7e9e5c8d4ee2e10dc9f1617c6"
            )
        )
        memes.add(
            MemeInfo(
                name = "Natalka Pralka",
                description = "4chan",
                url = "https://i.redd.it/esj4i04bcsw21.png"
            )
        )
        memes.add(
            MemeInfo(
                name = "Piotr Pietro",
                description = "Hot Weels",
                url = "https://preview.redd.it/5ikldesz1tw21.jpg?width=960&crop=smart&auto=webp&s=cf904ad656f6b88957be3b287fa4fdc7a5c01f23"
            )
        )
        memes.add(
            MemeInfo(
                name = "Piotr Skowyrski",
                description = "Hot Weels",
                url = "https://preview.redd.it/b86lwcgf2tw21.jpg?width=640&crop=smart&auto=webp&s=f8a7beaa2b04140beabdecd39f7cee94e320b5f5"
            )
        )

        return memes
    }
    /* SWIPE */

    /* DATABASE FUNCTIONS */
    // (MJ) Load Memes from Database Function
    private fun loadMemes() {

        memeList.clear()
        for(Meme in memeDatabase!!.getMeme().getMemeListRatingSorted()){
            memeList.add(
                MemeInfo(
                    name = memeDatabase!!.getMeme().getMemeItem(Meme.tId).name,
                    description = memeDatabase!!.getMeme().getMemeItem(Meme.tId).description,
                    url = memeDatabase!!.getMeme().getMemeItem(Meme.tId).url
                )
            )
        }
        reload()
    }

    private fun reload() {
        val old = adapter.getSpots()
        val new = createSpots()
        val callback = MemeDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    // (MJ) Temporary dialog box to input memes into database with name & url
    private fun temporarySimpleMemeAddition() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add New Meme")

        // https://stackoverflow.com/questions/10695103/creating-custom-alertdialog-what-is-the-root-view
        val view = layoutInflater.inflate(R.layout.dialog_new_image, null)
        val description = view.findViewById(R.id.descriptionInput) as EditText
        val url = view.findViewById(R.id.urlInput) as EditText
        builder.setView(view)

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val titleNew = description.text.toString()
            val urlNew = url.text.toString()
            var isValid = true
            if (titleNew.isBlank() || urlNew.isBlank()) {
                isValid = false
                dialog.dismiss()
            }
            if (isValid) {
                val meme = Meme(url = urlNew, name = titleNew, description = "")
                memeDatabase!!.getMeme().saveMeme(meme)
                onResume()
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
    /* DATABASE FUNCTIONS */
}
