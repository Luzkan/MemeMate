package com.codecrew.mememate.ui.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.codecrew.mememate.fragment.*

// (MJ) Return fragment to adapter based on position
class Pager(fm: FragmentManager, private var tabCount: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> TopFragment()
            1 -> Fragment()
            2 -> BrowseFragment()
            3 -> AddMemeFragment()
            4 -> LoggedUserProfileFragment()
            5 -> UserProfileFragment()
            else -> null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }
}
