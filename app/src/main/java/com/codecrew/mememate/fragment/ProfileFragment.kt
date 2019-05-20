package com.codecrew.mememate.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.profile.GalleryAdapter
import com.codecrew.mememate.activity.profile.GalleryMemeClickListener
import com.codecrew.mememate.database.models.MemeModel
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

private const val SPAN_COUNT = 3

class ProfileFragment : Fragment(), GalleryMemeClickListener {

    private var memesList = ArrayList<MemeModel>()
    private lateinit var galleryAdapter: GalleryAdapter
    private var currentPosition: Int = 0

    lateinit var recyclerView: RecyclerView
    lateinit var mainMeme : RoundedImageView



    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the adapter.
        galleryAdapter = GalleryAdapter(memesList)
        galleryAdapter.listener = this

        // Main meme
//        main_meme.setOnClickListener{mainMemeListener()}

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // (SG) Find widgets
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        recyclerView = v.findViewById(R.id.recyclerView) as RecyclerView
        mainMeme = v.findViewById(R.id.main_meme) as RoundedImageView

        // Set up ReclyclerView.
        recyclerView.layoutManager = GridLayoutManager(this.context, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

        // Load memes
        loadDemoMemes()
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }


    override fun onGalleryMemeClickListener(position: Int) {
        currentPosition = position

        Picasso.get()
            .load(memesList[currentPosition].url)
            .into(mainMeme)
    }

//    private fun mainMemeListener() {
//        val bundle = Bundle()
//        bundle.putSerializable("images", memesList)
//        bundle.putInt("position", currentPosition)
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        val galleryFragment = GalleryFullscreenFragment()
//        galleryFragment.arguments = bundle
//        galleryFragment.show(fragmentTransaction, "gallery")
//    }
    // For demonstration purpose only, we will get the memes for every user from the database.
    private fun loadDemoMemes() {
        val memes = resources.getStringArray(R.array.memes)

        val mainMemeObject = MemeModel(url = memes[currentPosition], location = "", rate = 0, dbId = "test", seenBy = ArrayList())
        Picasso.get()
            .load(mainMemeObject.url)
            .into(mainMeme)

        for (meme in memes.drop(1)) {
            memesList.add(MemeModel(url = meme, location = "", rate = 0, dbId = "test", seenBy = ArrayList()))
        }
        galleryAdapter.notifyDataSetChanged()
    }
}