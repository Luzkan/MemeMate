package com.codecrew.mememate.activity.profile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.GridLayout
import android.widget.ImageView
import com.codecrew.mememate.MemeInfo
import com.codecrew.mememate.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

private const val SPAN_COUNT = 3

class ProfileActivity : AppCompatActivity(), GalleryMemeClickListener {

    private var memesList = ArrayList<MemeInfo>()
    private lateinit var galleryAdapter: GalleryAdapter
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up the adapter.
        galleryAdapter = GalleryAdapter(memesList)

        // Set up ReclyclerView.
        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

        // Load memes
        loadDemoMemes()
    }

    override fun onGalleryMemeClickListener(position: Int) {
        currentPosition = position
    }

    // For demonstration purpose only, we will get the memes for every user from the database.
    private fun loadDemoMemes() {
        val memes = resources.getStringArray(R.array.memes)

        val mainMeme = MemeInfo(url = memes[0], description = "", name = "")
        Picasso.get()
            .load(mainMeme.url)
            .into(main_meme)

        for (meme in memes.drop(1)) {
            memesList.add(MemeInfo(url = meme, description = "", name = ""))
        }
        galleryAdapter.notifyDataSetChanged()
    }
}
