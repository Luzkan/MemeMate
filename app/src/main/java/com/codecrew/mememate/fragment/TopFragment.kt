package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.profile.GalleryMemeClickListener
import com.codecrew.mememate.activity.top.TopAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.acitivity_top.*

class TopFragment : Fragment(), GalleryMemeClickListener {

    private lateinit var recyclerViewTop: RecyclerView

    // (SG) Database
    lateinit var database: FirebaseFirestore

    private var memesList = ArrayList<MemeModel>()
    private lateinit var topAdapter: TopAdapter
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // (SG) Find the recycler view
        val v = inflater.inflate(R.layout.fragment_top, container, false)
        recyclerViewTop = v.findViewById(R.id.recyclerViewTop) as RecyclerView

        // (SG) Create Adapter
        topAdapter = TopAdapter(memesList)
        topAdapter.listener = this

        loadDemoMemes()

        // Set up ReclyclerView.
        recyclerViewTop.layoutManager = LinearLayoutManager(this.context)
        recyclerViewTop.adapter = topAdapter

        return v
    }

    override fun onGalleryMemeClickListener(position: Int) {
//        currentPosition = position
//
//        val bundle = Bundle()
//        bundle.putSerializable("images", memesList)
//        bundle.putInt("position", position)
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        val galleryFragment = GalleryFullscreenFragment()
//        galleryFragment.arguments = bundle
//        galleryFragment.show(fragmentTransaction, "top")
    }

    private fun loadDemoMemes() {
        database.collection("Memes").orderBy("rate").limit(10).get().addOnSuccessListener { memeCollection ->
            for (meme in memeCollection) {
                memesList.add(
                    MemeModel(
                        url = meme["url"].toString(),
                        location = meme["location"].toString(),
                        rate = meme["rate"].toString().toInt(),
                        seenBy = meme["seenBy"] as ArrayList<String>,
                        dbId = meme.id,
                        addedBy = meme["addedBy"].toString()
                    )
                )
            }
            memesList.reverse()
            topAdapter.notifyDataSetChanged()
        }
    }
}