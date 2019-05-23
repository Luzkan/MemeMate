package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.adapter.TopAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.interfaces.GalleryMemeClickListener
import com.google.firebase.firestore.FirebaseFirestore

class TopFragment : Fragment(), GalleryMemeClickListener {

    private lateinit var recyclerViewTop: RecyclerView

    // (SG) Database
    lateinit var database: FirebaseFirestore

    private var memesList = ArrayList<MemeModel>()
    private lateinit var topAdapter: TopAdapter
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseFirestore.getInstance()

        if ((activity as MainActivity).globalTopMemes == null) {
            memesList = ArrayList()
            (activity as MainActivity).globalTopMemes = memesList
        } else {
            memesList = (activity as MainActivity).globalTopMemes!!
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // (SG) Find the recycler view
        val v = inflater.inflate(R.layout.fragment_top, container, false)
        recyclerViewTop = v.findViewById(R.id.recyclerViewTop) as RecyclerView

        // (SG) Create Adapter
        topAdapter = TopAdapter(memesList)
        topAdapter.listener = this

        loadMemes()

        // Set up ReclyclerView.
        recyclerViewTop.layoutManager = LinearLayoutManager(this.context)
        recyclerViewTop.adapter = topAdapter

        loadMemes()
        return v
    }

    override fun onGalleryMemeClickListener(position: Int, memes: ArrayList<MemeModel>) {
        currentPosition = position

        val bundle = Bundle()
        bundle.putSerializable("images", memes)
        bundle.putInt("position", position)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "top")
    }

    private fun loadMemes() {
        database.collection("Memes").orderBy("rate").get().addOnSuccessListener { memeCollection ->
            val tempMemeList = ArrayList<MemeModel>()
            for (meme in memeCollection) {
                tempMemeList.add(
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
            tempMemeList.reverse()
            memesList.clear()
            memesList.addAll(tempMemeList)
            topAdapter.notifyDataSetChanged()
        }
    }
}