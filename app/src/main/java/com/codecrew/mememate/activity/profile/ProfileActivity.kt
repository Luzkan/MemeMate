//package com.codecrew.mememate.activity.profile
//
//import android.os.Bundle
//import android.support.v7.app.AppCompatActivity
//import android.support.v7.widget.GridLayoutManager
//import com.codecrew.mememate.R
//import com.codecrew.mememate.database.models.MemeModel
//import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.activity_profile.*
//import kotlin.random.Random
//
//private const val SPAN_COUNT = 3
//
//class ProfileActivity : AppCompatActivity(), GalleryMemeClickListener {
//
//    private var memesList = ArrayList<MemeModel>()
//    private lateinit var galleryAdapter: GalleryAdapter
//    private var currentPosition: Int = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile)
//
//        // Set up the adapter.
//        galleryAdapter = GalleryAdapter(memesList)
//        galleryAdapter.listener = this
//
//        // Set up ReclyclerView.
//        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
//        recyclerView.adapter = galleryAdapter
//
//        // Load memes
//        loadDemoMemes()
//
//        // Main meme
//        main_meme.setOnClickListener{mainMemeListener()}
//    }
//
//    override fun onGalleryMemeClickListener(position: Int) {
//        currentPosition = position
//
//        Picasso.get()
//            .load(memesList[currentPosition].url)
//            .into(main_meme)
//    }
//
//    private fun mainMemeListener() {
//        val bundle = Bundle()
//        bundle.putSerializable("images", memesList)
//        bundle.putInt("position", currentPosition)
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        val galleryFragment = GalleryFullscreenFragment()
//        galleryFragment.arguments = bundle
//        galleryFragment.show(fragmentTransaction, "gallery")
//    }
//    // For demonstration purpose only, we will get the memes for every user from the database.
//    private fun loadDemoMemes() {
//        val memes = resources.getStringArray(R.array.memes)
//
//        val mainMeme = MemeModel(url = memes[currentPosition], location = "", rate = 0, dbId = "test", seenBy = ArrayList())
//        Picasso.get()
//            .load(mainMeme.url)
//            .into(main_meme)
//
//        for (meme in memes.drop(1)) {
//            memesList.add(MemeModel(url = meme, location = "", rate = 0, dbId = "test", seenBy = ArrayList()))
//        }
//        galleryAdapter.notifyDataSetChanged()
//    }
//}
