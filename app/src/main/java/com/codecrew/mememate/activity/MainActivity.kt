package com.codecrew.mememate.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.codecrew.mememate.R
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.codecrew.mememate.fragment.ProfileFragment
import com.codecrew.mememate.ui.main.CustomViewPager
import com.codecrew.mememate.ui.main.Pager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //todo zapisywac model aktaulnie zalogowanego uzytkownika do sharedPrefs

    // (SG) Meme list
    var globalMemeList: ArrayList<MemeModel>? = null

    // (SG) Memes added by user
    var globalUserMemes: ArrayList<MemeModel>? = null

    // (PR) Memes liked by user
    var globalLikedMemes: ArrayList<MemeModel>? = null

    // (SG) Top meme list
    var globalTopMemes: ArrayList<MemeModel>? = null

    // (SG) Friends list
    var globalFriends: ArrayList<UserModel>? = null

    // (KS) properties to manage addMeme
    var isValid = false
    lateinit var pic: Uri

    var currentPanel = 1

    // (SG) Fragment manager
    val fragmentManager: FragmentManager = supportFragmentManager

    // (MJ) Fragment Pager View
    private var mTabLayout: TabLayout? = null
    var mViewPager: CustomViewPager? = null

    // (SG) Current user
    private lateinit var currentUser: FirebaseUser

    //(SG) Current user model
    private lateinit var currentUserModel: UserModel

    // (SG) Firebase instance
    private lateinit var db: FirebaseFirestore

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_top -> {
                if (currentPanel != 1) {
                    currentPanel = 1
                    mViewPager!!.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.navigation_friends -> {
                if (currentPanel != 2) {
                    currentPanel = 2
                    mViewPager!!.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.navigation_main -> {
                if (currentPanel != 3) {
                    currentPanel = 3
                    mViewPager!!.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.navigation_add -> {
                if (currentPanel != 4) {
                    currentPanel = 4
                    mViewPager!!.currentItem = 3
                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.navigation_profile -> {
                if (currentPanel != 5) {
                    currentPanel = 5
                    mViewPager!!.currentItem = 4
                    return@OnNavigationItemSelectedListener true
                }
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        pic = Uri.parse("android.resource://" + this.packageName + "/" + R.drawable.default_meme_add)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // (SG)
        db = FirebaseFirestore.getInstance()

        currentUser = FirebaseAuth.getInstance().currentUser!!

        Log.d("MEMESKI", "POBIERAM USERA")
        db.document("Users/${currentUser.uid}")
            .get()
            .addOnSuccessListener {
                Log.d("MEMESKI", "POBRAŁEM USERA")
                currentUserModel = UserModel(
                    uid = it["uid"].toString(),
                    email = it["email"].toString(),
                    userName = it["userName"].toString(),
                    likedMemes = it["likedMemes"] as ArrayList<String>?,
                    addedMemes = it["addedMemes"] as ArrayList<String>?,
                    following = it["following"] as ArrayList<String>?,
                    followers = it["followers"] as ArrayList<String>?
                )
//                loadData()
            }

        // (MJ) Pager Adapter
        // (MJ) Tab ID
        mTabLayout = findViewById<View>(R.id.tabs) as TabLayout
        mTabLayout!!.setupWithViewPager(mViewPager)

        // (MJ) Add Upper Tabs (they are invisible [gone] in layout, needed for swipe feature.
        // --> IMPORTANT <-- Matches have "profile" function now due to lack of Matches Fragment
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Top"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Friends"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Browse"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Add"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Profile"))
        mTabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        // (MJ) Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container)
        val adapter = Pager(supportFragmentManager, mTabLayout!!.tabCount)
        mViewPager!!.adapter = adapter

        mViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                currentPanel = position + 1

                // (MJ) Disables Touch on browse memes fragment
                if (position == 2) {
                    mViewPager!!.disableTouches()
                } else {
                    mViewPager!!.enableTouches()
                }
            }

            override fun onPageSelected(position: Int) {
                mTabLayout!!.setScrollPosition(position, 0F, true)
                mTabLayout!!.isSelected = true

                // (MJ) Toggles "Checked" button on navbar depending on scrolled page
                nav_view.menu.getItem(position).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        val navView: BottomNavigationView = findViewById(R.id.nav_view)



        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        //displayBrowsing()
        //navView.selectedItemId = R.id.navigation_top
    }

    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }

    // (KS) Hiding keyboard when click outside the EditText
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    // (SG) Current user getter
    fun getCurrentUser(): UserModel {
        return currentUserModel
    }


    // (SG) todo CHANGE PROFILE FRAGMENT TO FR
    fun displayProfile(uid: String) {
        val transaction = fragmentManager.beginTransaction()
        val fragment = ProfileFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun loadData() {
        downloadFriends()
//        downloadAddedMemes()
//        downloadLikedMemes()
//        downloadBrowseMemes()
    }

    private fun downloadBrowseMemes() {

        globalMemeList = ArrayList()
        db.collection("Memes").get().addOnSuccessListener {
            // (SG) Casting downloaded memes into objects
            for (meme in it) {
                val newMeme = MemeModel(
                    dbId = meme.id,
                    url = meme["url"].toString(),
                    location = meme["location"].toString(),
                    rate = meme["rate"].toString().toInt(),
                    seenBy = meme["seenBy"] as ArrayList<String>,
                    addedBy = meme["addedBy"].toString(),
                    userID = meme["userID"].toString()

                )
                if (!newMeme.seenBy.contains(currentUser.uid) && !globalMemeList!!.contains(newMeme)) {
                    globalMemeList!!.add(newMeme)
                }
            }
        }
    }

    private fun downloadAddedMemes() {
        globalUserMemes = ArrayList()
        db.collection("Memes")
            .whereEqualTo("userId", currentUserModel.uid)
            .get()
            .addOnSuccessListener { memeCollection ->
                for (meme in memeCollection) {
                    val memeObject = MemeModel(
                        url = meme["url"].toString(),
                        location = meme["location"].toString(),
                        rate = meme["rate"].toString().toInt(),
                        seenBy = meme["seenBy"] as ArrayList<String>,
                        dbId = meme.toString(),
                        addedBy = meme["addedBy"].toString(),
                        userID = meme["userID"].toString()
                    )
                    globalUserMemes!!.add(memeObject)
                }
            }
    }


    private fun downloadLikedMemes() {
        globalLikedMemes = ArrayList()
        currentUserModel.likedMemes?.forEach { likedMeme ->
            db.document("Memes/$likedMeme")
                .get()
                .addOnSuccessListener { meme ->
                    globalUserMemes!!.add(
                        MemeModel(
                            url = meme["url"].toString(),
                            location = meme["location"].toString(),
                            rate = meme["rate"].toString().toInt(),
                            seenBy = meme["seenBy"] as ArrayList<String>,
                            dbId = meme.toString(),
                            addedBy = meme["addedBy"].toString(),
                            userID = meme["userID"].toString()
                        )
                    )
                }
        }
    }

    private fun downloadFriends() {
        globalFriends = ArrayList()
        currentUserModel.following!!.forEach { friendID ->
            db.document("Users/$friendID").get().addOnSuccessListener {
                val friend = UserModel(
                    uid = it["uid"].toString(),
                    email = it["email"].toString(),
                    userName = it["username"].toString(),
                    likedMemes = it["likedMemes"] as ArrayList<String>?,
                    addedMemes = it["addedMemes"] as ArrayList<String>?,
                    following = it["following"] as ArrayList<String>?,
                    followers = it["followers"] as ArrayList<String>?
                )
                globalFriends!!.add(friend)
            }
        }
    }
}

/* Legacy Code:

    private fun createMemes() {
        val memeUrl = ArrayList<String>()
        memeUrl.add("https://s.newsweek.com/sites/www.newsweek.com/files/styles/md/public/2018/10/18/obesity-meme.png")
        memeUrl.add("https://i.redd.it/wp1jwvrqekz21.jpg")
        memeUrl.add("https://i.redd.it/lqkp9slwokz21.png")
        memeUrl.add("https://i.redd.it/e03i956pkjz21.jpg")
        memeUrl.add("https://external-preview.redd.it/WuxQTOvkvPlngfAJd-Kxuja_bKb5Eyvsg1oP-UsfE70.jpg?auto=webp&s=680d0ab4819e4b5a60a180afb275d5721e3e05b1")
        memeUrl.add("https://i.redd.it/gzpa64l6xjz21.jpg")
        memeUrl.add("https://i.redd.it/1ygcawowwjz21.jpg")
        memeUrl.add("https://i.redd.it/nhepplghxjz21.jpg")
        memeUrl.add("https://i.redd.it/lcoit8ygnkz21.jpg")
        memeUrl.add("https://i.redd.it/wh46luncsjz21.jpg")
        memeUrl.add("https://external-preview.redd.it/Ho2XSQOhaHGN3LhkLnPAf2OTkXwtuBTKQ9FXgdumH-I.jpg?auto=webp&s=6747effd23f9a9f3072353652f18bfc2fc59a0f5")
        memeUrl.add("https://i.redd.it/3r5dwuiurjz21.jpg")
        memeUrl.add("https://i.redd.it/nbj35tkgikz21.jpg")
        memeUrl.add("https://i.redd.it/ez9u638kxnz21.jpg")
        memeUrl.add("https://i.redd.it/5z9k4d014oz21.jpg")
        memeUrl.add("https://i.redd.it/kdryu8xktnz21.jpg")
        memeUrl.add("https://i.redd.it/a1tkevfp7pz21.png")
        memeUrl.add("https://i.redd.it/tdyfd89k5pz21.jpg")
        memeUrl.add("https://i.redd.it/pzqjcocjxoz21.jpg")
        memeUrl.add("https://i.redd.it/19874ij79pz21.jpg")
        memeUrl.add("https://i.redd.it/mc6beqqr4pz21.jpg")
        memeUrl.add("https://i.redd.it/51qsorieloz21.jpg")

        val database = FirebaseFirestore.getInstance()

        memeUrl.forEachIndexed { index, it ->
            val meme = HashMap<String, Any>()
            meme["url"] = it
            meme["title"] = index.toString()
            meme["seenBy"] = arrayListOf(currentUser.uid)
            meme["userId"] = currentUser.uid
            meme["rate"] = 0
            meme["location"] = "location.downloaded.from.phone"
            meme["addedBy"] = currentUser.displayName.toString()
            database.collection("Memes").add(meme)
                .addOnSuccessListener {
                    database.collection("Users").document(currentUser.uid)
                        .update("addedMemes", FieldValue.arrayUnion(it.id))
                }
        }
    }

    // (KS) Choosing side to make swipe animation when changing fragment
    private fun swipeSide(transaction: FragmentTransaction, src: Int, target: Int) {
        if (src < target) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        currentPanel = target
    }

    /* FRAGMENTS */
    private fun displayTop() {
        val transaction = fragmentManager.beginTransaction()
        swipeSide(transaction, currentPanel, 1)
        val fragment = TopFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun displayFriends() {
        val transaction = fragmentManager.beginTransaction()
        swipeSide(transaction, currentPanel, 2)
        val fragment = FriendsFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun displayBrowsing() {
        val transaction = fragmentManager.beginTransaction()
        swipeSide(transaction, currentPanel, 3)
        val fragment = BrowseFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun displayAddMeme() {
        val transaction = fragmentManager.beginTransaction()
        swipeSide(transaction, currentPanel, 4)
        val fragment = AddMemeFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun displayProfile() {
        val transaction = fragmentManager.beginTransaction()
        swipeSide(transaction, currentPanel, 5)
        val fragment = ProfileFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

 */