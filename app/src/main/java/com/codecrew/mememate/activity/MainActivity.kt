package com.codecrew.mememate.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.codecrew.mememate.R
import com.codecrew.mememate.fragment.AddMemeFragment
import com.codecrew.mememate.fragment.BrowseFragment
import com.codecrew.mememate.fragment.ProfileFragment
import com.codecrew.mememate.fragment.TopFragment


class MainActivity : AppCompatActivity() {

    // (KS) properties to manage addMeme
    var isValid = false
    lateinit var pic : Uri

    var currentPanel = 3

    // (SG) Fragment manager
    private val fragmentManager: FragmentManager = supportFragmentManager

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_top -> {
                textMessage.setText(R.string.top)
                displayTop()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_matches -> {
                textMessage.setText(R.string.matches)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_main -> {
                textMessage.setText(R.string.memes)
                displayBrowsing()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_add -> {
                textMessage.setText(R.string.add)
                displayAddMeme()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                textMessage.setText(R.string.profile)
                displayProfile()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        pic = Uri.parse("android.resource://" + this.packageName + "/" + R.drawable.default_meme_add)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        displayBrowsing()
        navView.selectedItemId = R.id.navigation_main
    }

    override fun onBackPressed() {
        this.moveTaskToBack(true)
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

    fun displayProfile() {
        val transaction = fragmentManager.beginTransaction()
        swipeSide(transaction, currentPanel, 5)
        val fragment = ProfileFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    //(KS) Hiding keyboard when click outside the EditText
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    //(KS) Choosing side to make swipe animation when changing fragment
    private fun swipeSide(transaction: FragmentTransaction, src: Int, target: Int) {
        if (src < target) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        currentPanel = target
    }

}
