package com.codecrew.mememate.fragment

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
import com.codecrew.mememate.adapter.GalleryAdapter
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.codecrew.mememate.interfaces.FragmentCallBack
import com.codecrew.mememate.interfaces.MemeClickListener
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso

private const val SPAN_COUNT = 3

@Suppress("UNCHECKED_CAST")
abstract class ProfileFragment(private val layoutRes: Int) : Fragment(),
    MemeClickListener, FragmentCallBack {

    protected lateinit var likedMemesList: ArrayList<MemeModel>
    protected lateinit var userMemesList: ArrayList<MemeModel>

    private var currentPosition: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var mainMeme: RoundedImageView
    private lateinit var viewSwitchButton: ImageButton

    private var viewType = ViewType.ADDED

    // Adapters
    private lateinit var userMemesAdapter: GalleryAdapter
    private lateinit var likedMemesAdapter: GalleryAdapter

    // (SG) Database
    protected lateinit var database: FirebaseFirestore
    protected lateinit var userID: String

    // (SG) User data fields
    private lateinit var username: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creating database instance and current user
        database = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Creating database instance and current user
        database = FirebaseFirestore.getInstance()
        // (SG) Find widgets
        val v = inflater.inflate(this.layoutRes, container, false)
        recyclerView = v.findViewById(R.id.recyclerView) as RecyclerView
        mainMeme = v.findViewById(R.id.main_meme) as RoundedImageView
        username = v.findViewById(R.id.item_name)

        // (PR) Switch view button
        viewSwitchButton = v.findViewById(R.id.switch_view_button)
        viewSwitchButton.setOnClickListener { switchView() }

        // (PR) Adapters
        userMemesAdapter = GalleryAdapter(userMemesList).also { it.listener = this }
        likedMemesAdapter = GalleryAdapter(likedMemesList).also { it.listener = this }

        // (PR) Set up RecyclerView.
        recyclerView.layoutManager = GridLayoutManager(this.context, SPAN_COUNT) as RecyclerView.LayoutManager?
        recyclerView.adapter = userMemesAdapter

        // (SG) Set up user data
        database.document("Users/$userID")
            .get()
            .addOnSuccessListener {
                username.text = it["userName"] as CharSequence?
            }

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
                currentPosition = 0
                if (likedMemesList.size > 0) {
                    Picasso.get().load(likedMemesList[0].url).into(mainMeme)
                } else {
                    displayDefaultProfile()
                }
            }
            ViewType.LIKED -> {
                viewType = ViewType.ADDED
                viewSwitchButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.added_icon))
                recyclerView.adapter = userMemesAdapter
                currentPosition = 0
                if (userMemesList.size > 0) {
                    Picasso.get().load(userMemesList[0].url).into(mainMeme)
                } else {
                    displayDefaultProfile()
                }
            }
        }
    }

    override fun onMemeClick(position: Int, memes: ArrayList<MemeModel>) {
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
        galleryFragment.setTargetFragment(this, 2137)
        galleryFragment.show(fragmentTransaction, "gallery")
    }

    override fun onDestroy() {
//        (activity as MainActivity).globalUserMemes = userMemesList
        super.onDestroy()
    }

    protected fun loadUserMemes() {
        database.document("Users/$userID").get().addOnSuccessListener {

            val userMemes = ArrayList<MemeModel>()

            database.collection("Memes")
                .whereEqualTo("userId", userID)
                .get()
                .addOnSuccessListener { memeCollection ->
                    for (meme in memeCollection) {
                        val memeObject = MemeModel(
                            url = meme["url"].toString(),
                            location = meme["location"].toString(),
                            rate = meme["rate"].toString().toInt(),
                            seenBy = meme["seenBy"] as ArrayList<String>,
                            dbId = meme.toString(),
                            addedBy = meme["addedBy"].toString(),
                            userId = meme["userId"].toString()
                        )
                        userMemes.add(memeObject)
                    }
                    this.userMemesList.addAll(userMemes)
                    userMemesAdapter.notifyDataSetChanged()
                    if (userMemesList.size == 0) {
                        displayDefaultProfile()
                        //todo wyświetl powiadomienie, np takie jak na ig w miejscu gdzie normalnie znajdują się zdjęcia

                    } else {
                        displayLastMeme(currentPosition)
                        userMemesAdapter.notifyDataSetChanged()
                    }
                }
        }
    }

    // (PR) Loading liked memes
    protected fun loadLikedMemes() {
        database.document("Users/$userID")
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
                                    addedBy = meme["addedBy"].toString(),
                                    userId = meme["userId"].toString()
                                )
                            )
                        }
                }

                likedMemesAdapter.notifyDataSetChanged()
            }
    }

    private fun displayDefaultProfile() {
        Picasso.get()
            .load(getString(R.string.default_meme))
            .into(mainMeme)
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

    override fun onAction(position: Int) {
        currentPosition = position
        Picasso.get()
            .load(
                when (viewType) {
                    ViewType.LIKED -> likedMemesList[position].url
                    ViewType.ADDED -> userMemesList[position].url
                }
            ).into(mainMeme)
    }
}