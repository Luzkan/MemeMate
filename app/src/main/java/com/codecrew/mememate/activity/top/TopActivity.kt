package com.codecrew.mememate.activity.top

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.profile.GalleryFullscreenFragment
import com.codecrew.mememate.activity.profile.GalleryMemeClickListener
import com.codecrew.mememate.database.models.MemeModel
import kotlinx.android.synthetic.main.acitivity_top.*

class TopActivity : AppCompatActivity(), GalleryMemeClickListener {

    private var memesList = ArrayList<MemeModel>()
    private lateinit var topAdapter: TopAdapter
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_top)

        // Set up the adapter.
        topAdapter = TopAdapter(memesList)
        topAdapter.listener = this

//        // Set up ReclyclerView.
        recyclerViewTop.layoutManager = LinearLayoutManager(this)
        recyclerViewTop.adapter = topAdapter

//        // Load memes
//        loadDemoMemes()
    }

    override fun onGalleryMemeClickListener(position: Int, memes: ArrayList<MemeModel>) {
        currentPosition = position

        val bundle = Bundle()
        bundle.putSerializable("images", memes)
        bundle.putInt("position", position)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "top")
    }

    // For demonstration purpose only, we will get the memes for every user from the database.
//    private fun loadDemoMemes() {
//        val memes = resources.getStringArray(R.array.memes)
//
//        for (meme in memes.drop(1)) {
//            memesList.add(MemeModel(url = meme, location = "", rate = 0, dbId = "test", seenBy = ArrayList(), addedBy = ))
//        }
//        topAdapter.notifyDataSetChanged()
//    }
}