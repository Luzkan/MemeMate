package com.codecrew.mememate.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.profile.GalleryMemeClickListener
import com.codecrew.mememate.activity.top.TopAdapter
import com.codecrew.mememate.database.models.MemeModel

class TopFragment : Fragment(), GalleryMemeClickListener {

    lateinit var recyclerViewTop : RecyclerView

    private var memesList = ArrayList<MemeModel>()
    private lateinit var topAdapter: TopAdapter
    private var currentPosition: Int = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        val memes = resources.getStringArray(R.array.memes)

        for (meme in memes.drop(1)) {
            memesList.add(MemeModel(url = meme, location = "", rate = 0, dbId = "test", seenBy = ArrayList()))
        }
        topAdapter.notifyDataSetChanged()
    }


}