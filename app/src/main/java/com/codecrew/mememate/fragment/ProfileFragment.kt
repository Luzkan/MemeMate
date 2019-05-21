package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.profile.GalleryAdapter
import com.codecrew.mememate.activity.profile.GalleryMemeClickListener
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

private const val SPAN_COUNT = 3

class ProfileFragment : Fragment(), GalleryMemeClickListener {

    private var memesList = ArrayList<MemeModel>()
    private lateinit var galleryAdapter: GalleryAdapter
    private var currentPosition: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var mainMeme: RoundedImageView


    // (SG) Database
    private lateinit var database: FirebaseFirestore
    private lateinit var user: FirebaseUser

    //(SG) User data fields
    private lateinit var location : TextView
    private lateinit var username : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creating database instance and current user
        database = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!

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
        username = v.findViewById(R.id.item_name)
        location = v.findViewById(R.id.item_city)

        // Set up RecyclerView.
        recyclerView.layoutManager = GridLayoutManager(this.context, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

        // Load memes
        loadMemes()

        // (SG) Set up user data
        username.text = user.displayName

        return v
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
    private fun loadMemes() {

        database.document("Users/${user.uid}").get().addOnSuccessListener {
            // (SG) Casting downloaded memes into objects
            val userModel = UserModel(
                uid = it["uid"].toString(),
                email = it["email"].toString(),
                userName = it["username"].toString(),
                lickedMemes = it["lickedMemems"] as ArrayList<String>?,
                addedMemes = it["addedMemes"] as ArrayList<String>?
            )

            //todo pobieramy całą kolekcję memów (jedno duże zapytanie) czy pobieramy tylko memy danego użytkownika (dużo zapytań)?

            val userMemes = ArrayList<MemeModel>()

            database.collection("Memes").get().addOnSuccessListener { memeCollection ->
                for (meme in memeCollection) {
                    userModel.addedMemes?.forEach { memeID ->
                        if (meme.id == (memeID)) {
                            val memeObject = MemeModel(
                                url = meme["url"].toString(),
                                location = meme["location"].toString(),
                                rate = meme["rate"].toString().toInt(),
                                seenBy = meme["seenBy"] as ArrayList<String>,
                                dbId = memeID,
                                addedBy = meme["addedBy"].toString()
                            )
                            userMemes.add(memeObject)
                        }
                    }
                }
                memesList.addAll(userMemes)

                if (userMemes.size == 0) {
                    //todo wyświetl powiadomienie, np takie jak na ig

                } else {
                    val currentMeme = memesList[currentPosition]
                    Picasso.get()
                        .load(currentMeme.url)
                        .into(mainMeme)

                    location.text = currentMeme.location
                    galleryAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
