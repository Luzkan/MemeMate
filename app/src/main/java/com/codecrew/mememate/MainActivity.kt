package com.codecrew.mememate

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {

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
    }
}
