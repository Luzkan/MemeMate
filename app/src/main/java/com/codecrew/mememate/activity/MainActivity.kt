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
import com.codecrew.mememate.ui.main.CustomViewPager
import com.codecrew.mememate.ui.main.Pager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // (SG) Meme list
    var globalMemeList: ArrayList<MemeModel>? = null

    // (SG) Memes added by user
    var globalUserMemes: ArrayList<MemeModel>? = null

    // (PR) Memes liked by user
    var globalLikedMemes: ArrayList<MemeModel>? = null

    var clickedUserMemesList: ArrayList<MemeModel>? = null
    var clickedUserLikedMemesList: ArrayList<MemeModel>? = null

    // (SG) Top meme List
    var globalTopMemes: ArrayList<MemeModel>? = null

    // (KS) properties to manage addMeme
    var isValid = false
    lateinit var pic: Uri

//    private var currentPanel = 1

    // (SG) Fragment manager
    val fragmentManager: FragmentManager = supportFragmentManager

    // (MJ) Fragment Pager View
    private var mTabLayout: TabLayout? = null
    var mViewPager: CustomViewPager? = null

    // (SG) Current user
    private lateinit var currentUser: FirebaseUser

    // (PR) Clicked user profile
    var clickedUserNameID: String? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_top -> {
                mViewPager!!.currentItem = 0
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_matches -> {
                mViewPager!!.currentItem = 1
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_main -> {
                mViewPager!!.currentItem = 2
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_add -> {
                mViewPager!!.currentItem = 3
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_profile -> {
                mViewPager!!.currentItem = 4
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        pic = Uri.parse("android.resource://" + this.packageName + "/" + R.drawable.default_meme_add)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // (MJ) Pager Adapter
        // (MJ) Tab ID
        mTabLayout = findViewById<View>(R.id.tabs) as TabLayout
        mTabLayout!!.setupWithViewPager(mViewPager)

        // (MJ) Add Upper Tabs (they are invisible [gone] in layoutRes, needed for swipe feature.
        // --> IMPORTANT <-- Matches have "profile" function now due to lack of Matches Fragment
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Top"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Matches"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Browse"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Add"))
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Profile"))
        mTabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        // (MJ) Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container)
        val adapter = Pager(supportFragmentManager, mTabLayout!!.tabCount + 1)
        mViewPager!!.adapter = adapter

        mViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                // (MJ) Disables Touch on browse memes fragment
                if (position in setOf(2, 5)) {
                    mViewPager!!.disableTouches()
                } else {
                    mViewPager!!.enableTouches()
                }
            }

            override fun onPageSelected(position: Int) {
                mTabLayout!!.setScrollPosition(position, 0F, true)
                mTabLayout!!.isSelected = true

                if (position in 0 until mTabLayout!!.tabCount) {
                    // (MJ) Toggles "Checked" button on navbar depending on scrolled page
                    nav_view.menu.getItem(position).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        currentUser = FirebaseAuth.getInstance().currentUser!!
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

    // (PR) Shows up profile of the user's nickname we clicked.
    fun goToClickedUsernameProfile(userID: String) {
        Log.d("Memes", "Username clicked ID: $userID")
        clickedUserNameID = userID
        this.clickedUserMemesList = null
        this.clickedUserLikedMemesList = null
        mViewPager?.currentItem = 5
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



 */