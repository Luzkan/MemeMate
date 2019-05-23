package com.codecrew.mememate.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.activity.SettingsActivity
import com.codecrew.mememate.adapter.GalleryAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.codecrew.mememate.interfaces.GalleryMemeClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

private const val SPAN_COUNT = 3

class ProfileFragment : Fragment(), GalleryMemeClickListener {

    private lateinit var likedMemesList: ArrayList<MemeModel>
    private lateinit var userMemesList: ArrayList<MemeModel>
    private var currentPosition: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var mainMeme: RoundedImageView

    private lateinit var settingsButton: ImageButton
    private lateinit var viewSwitchButton: ImageButton

    private var viewType = ViewType.ADDED
    private lateinit var userMemesAdapter: GalleryAdapter
    private lateinit var likedMemesAdapter: GalleryAdapter

    // (SG) Database
    private lateinit var database: FirebaseFirestore
    private lateinit var user: FirebaseUser

    // (SG) User data fields
    private lateinit var location: TextView
    private lateinit var username: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creating database instance and current user
        database = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!

        // (SG) If userMemes Array has not been downloaded yet (When it's first time we click profile tab)
        if ((activity as MainActivity).globalUserMemes == null) {
            userMemesList = ArrayList()
            loadUserMemes()
        } else {
            userMemesList = (activity as MainActivity).globalUserMemes!!
        }

        // (PR) Same as above but for likedMemes
        if ((activity as MainActivity).globalLikedMemes == null) {
            likedMemesList = ArrayList()
            loadLikedMemes()
        } else {
            likedMemesList = (activity as MainActivity).globalLikedMemes!!
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // (SG) Find widgets
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        recyclerView = v.findViewById(R.id.recyclerView) as RecyclerView
        mainMeme = v.findViewById(R.id.main_meme) as RoundedImageView
        username = v.findViewById(R.id.item_name)
        location = v.findViewById(R.id.item_city)

        // (PR) Settings button
        settingsButton = v.findViewById(R.id.settings_button)
        settingsButton.setOnClickListener { openSettingsActivity() }

        // (PR) Switch view button
        viewSwitchButton = v.findViewById(R.id.switch_view_button)
        viewSwitchButton.setOnClickListener { switchView() }

        // (PR) Adapters
        userMemesAdapter = GalleryAdapter(userMemesList).also { it.listener = this }
        likedMemesAdapter = GalleryAdapter(likedMemesList).also { it.listener = this }

        // (PR) Set up RecyclerView.
        recyclerView.layoutManager = GridLayoutManager(this.context, SPAN_COUNT)
        recyclerView.adapter = userMemesAdapter

        // (SG) Set up user data
        username.text = user.displayName

        // Main meme
        mainMeme.setOnClickListener { mainMemeListener() }


        // (SG) must be here because the imageView wont be initialized earlier
        if (userMemesList.size == 0) {
            displayDefaultProfile()
        } else {
            displayLastMeme(currentPosition)
        }

        return v
    }

    // (PR) Switching view type.
    private fun switchView() {
        when (viewType) {
            ViewType.ADDED -> {
                viewType = ViewType.LIKED
                viewSwitchButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.liked_icon))
                recyclerView.adapter = likedMemesAdapter
            }
            ViewType.LIKED -> {
                viewType = ViewType.ADDED
                viewSwitchButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.added_icon))
                recyclerView.adapter = userMemesAdapter
            }
        }
    }

    private fun openSettingsActivity() {
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onGalleryMemeClickListener(position: Int, memes: ArrayList<MemeModel>) {
        currentPosition = position
        Picasso.get()
            .load(memes[currentPosition].url)
            .into(mainMeme)
    }

    private fun mainMemeListener() {
        val bundle = Bundle()
        bundle.putSerializable(
            "images",
            when (viewType) {
                ViewType.ADDED -> userMemesList
                ViewType.LIKED -> likedMemesList
            }
        )
        bundle.putInt("position", currentPosition)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "gallery")
    }

    override fun onDestroy() {
        (activity as MainActivity).globalUserMemes = userMemesList
        super.onDestroy()
    }

    private fun loadUserMemes() {
        database.document("Users/${user.uid}").get().addOnSuccessListener {
            // (SG) Casting downloaded memes into objects
            val userModel = UserModel(
                uid = it["uid"].toString(),
                email = it["email"].toString(),
                userName = it["username"].toString(),
                likedMemes = it["likedMemes"] as ArrayList<String>?,
                addedMemes = it["addedMemes"] as ArrayList<String>?
            )

            //todo pobieramy całą kolekcję memów (jedno duże zapytanie) czy pobieramy tylko memy danego użytkownika (dużo zapytań)?

            val userMemes = ArrayList<MemeModel>()

            database.collection("Memes")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { memeCollection ->
                for (meme in memeCollection) {
                    val memeObject = MemeModel(
                        url = meme["url"].toString(),
                        location = meme["location"].toString(),
                        rate = meme["rate"].toString().toInt(),
                        seenBy = meme["seenBy"] as ArrayList<String>,
                        dbId = meme.toString(),
                        addedBy = meme["addedBy"].toString()
                    )
                    userMemes.add(memeObject)
                }
                userMemesList.addAll(userMemes)

                if (userMemesList.size == 0) {
                    displayDefaultProfile()
                    //todo wyświetl powiadomienie, np takie jak na ig w miejscu gdzie normalnie znajdują się zdjęcia

                } else {
                    displayLastMeme(currentPosition)
                    location.text = if (!userMemesList[currentPosition].location.startsWith("location.downloaded")) {
                        userMemesList[currentPosition].location
                    } else {
                        "User location."
                    }
                    userMemesAdapter.notifyDataSetChanged()
                }
                (activity as MainActivity).globalUserMemes = userMemesList
            }
        }
    }

    // (PR) Loading liked memes
    private fun loadLikedMemes() {
        database.document("Users/${user.uid}")
            .get()
            .addOnSuccessListener {
                val userModel = UserModel(
                    uid = it["uid"].toString(),
                    email = it["email"].toString(),
                    userName = it["username"].toString(),
                    likedMemes = it["likedMemes"] as ArrayList<String>?,
                    addedMemes = it["addedMemes"] as ArrayList<String>?
                )

                userModel.likedMemes?.forEach { likedMeme ->
                    database.document("Memes/$likedMeme")
                        .get()
                        .addOnSuccessListener { meme ->
                            likedMemesList.add(
                                MemeModel(
                                    url = meme["url"].toString(),
                                    location = meme["location"].toString(),
                                    rate = meme["rate"].toString().toInt(),
                                    seenBy = meme["seenBy"] as ArrayList<String>,
                                    dbId = meme.toString(),
                                    addedBy = meme["addedBy"].toString()
                                )
                            )
                            likedMemesAdapter.notifyDataSetChanged()
                            (activity as MainActivity).globalLikedMemes = likedMemesList
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
            .load(userMemesList[currentPosition].url)
            .into(mainMeme)
    }

    private enum class ViewType {
        ADDED,
        LIKED
    }
}
