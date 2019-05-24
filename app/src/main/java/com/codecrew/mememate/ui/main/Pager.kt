package com.codecrew.mememate.ui.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.fragment.*

// (MJ) Return fragment to adapter based on position
class Pager(fm: FragmentManager, private var tabCount: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return TopFragment()
            }
            1 -> {
                return FriendsFragment()
            }
            2 -> {
                return BrowseFragment()
            }
            3 -> {
                return AddMemeFragment()
            }
            4 -> {
                return ProfileFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }
}
