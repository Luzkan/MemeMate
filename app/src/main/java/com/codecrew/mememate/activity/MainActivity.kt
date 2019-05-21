package com.codecrew.mememate.activity


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.codecrew.mememate.R
import com.codecrew.mememate.fragment.AddMemeFragment
import com.codecrew.mememate.fragment.BrowseFragment
import com.codecrew.mememate.fragment.ProfileFragment
import com.codecrew.mememate.fragment.TopFragment


class MainActivity : AppCompatActivity() {


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
        val fragment = TopFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun displayBrowsing() {
        val transaction = fragmentManager.beginTransaction()
        val fragment = BrowseFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun displayAddMeme() {
        val transaction = fragmentManager.beginTransaction()
        val fragment = AddMemeFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun displayProfile() {
        val transaction = fragmentManager.beginTransaction()
        val fragment = ProfileFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
