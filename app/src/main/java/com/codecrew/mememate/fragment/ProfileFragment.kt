package com.codecrew.mememate.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.activity.profile.GalleryAdapter
import com.codecrew.mememate.activity.profile.GalleryFullscreenFragment
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

    private lateinit var memesList: ArrayList<MemeModel>
    private lateinit var galleryAdapter: GalleryAdapter
    private var currentPosition: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var mainMeme: RoundedImageView

    private lateinit var settingsButton: ImageButton

    // (SG) Database
    private lateinit var database: FirebaseFirestore
    private lateinit var user: FirebaseUser

    //(SG) User data fields
    private lateinit var location: TextView
    private lateinit var username: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creating database instance and current user
        database = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!

        //(SG) If userMemes Array has not been downloaded yet (When it's first time we click profile tab)
        if ((activity as MainActivity).globalUserMemes == null) {
            memesList = ArrayList()
            loadMemes()
            (activity as MainActivity).globalUserMemes = memesList
        } else {
            memesList = (activity as MainActivity).globalUserMemes!!
        }

        // Set up the adapter.
        galleryAdapter = GalleryAdapter(memesList)
        galleryAdapter.listener = this

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // (SG) Find widgets
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        recyclerView = v.findViewById(R.id.recyclerView) as RecyclerView
        mainMeme = v.findViewById(R.id.main_meme) as RoundedImageView
        username = v.findViewById(R.id.item_name)
        location = v.findViewById(R.id.item_city)

        settingsButton = v.findViewById(R.id.settings_button)
        settingsButton.setOnClickListener { openSettingDialog() }
        // Set up RecyclerView.
        recyclerView.layoutManager = GridLayoutManager(this.context, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

        // (SG) Set up user data
        username.text = user.displayName

        // Main meme
        mainMeme.setOnClickListener { mainMemeListener() }

        // (SG) must be here because the imageView wont be initialized earlier
        if (memesList.size == 0) {
            displayDefaultProfile()
        } else {
            displayLastMeme(currentPosition)
        }

        return v
    }

    private fun openSettingDialog() {
        // TODO: settings dialog
        Toast.makeText(context, "Test", Toast.LENGTH_LONG).show()
    }

    override fun onGalleryMemeClickListener(position: Int) {
        currentPosition = position

        Picasso.get()
            .load(memesList[currentPosition].url)
            .into(mainMeme)
    }

    private fun mainMemeListener() {
        val bundle = Bundle()
        bundle.putSerializable("images", memesList)
        bundle.putInt("position", currentPosition)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "gallery")
    }

    override fun onDestroy() {
        (activity as MainActivity).globalUserMemes = memesList
        super.onDestroy()
    }

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
                    displayDefaultProfile()
                    //todo wyświetl powiadomienie, np takie jak na ig w miejscu gdzie normalnie znajdują się zdjęcia

                } else {
                    displayLastMeme(currentPosition)
                    location.text = if (!memesList[currentPosition].location.startsWith("location.downloaded")) {
                        memesList[currentPosition].location
                    } else {
                        "User location."
                    }
                    galleryAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun displayDefaultProfile() {
        Picasso.get()
            .load(getString(R.string.default_meme))
            .into(mainMeme)

        location.text = "Add new meme :("
    }

    private fun displayLastMeme(currentPosition: Int) {
        Picasso.get()
            .load(memesList[currentPosition].url)
            .into(mainMeme)
    }
}
