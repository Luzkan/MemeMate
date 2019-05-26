package com.codecrew.mememate.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.codecrew.mememate.database.models.MemeModel
import com.codecrew.mememate.database.models.UserModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_meme_adding.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddMemeFragment : Fragment() {

    private val handler = Handler()

    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var memeUrl: String
    private lateinit var uri: Uri
    private lateinit var pic: ImageView

    private lateinit var user: UserModel

    private lateinit var confirmButton: CircularProgressButton
    private lateinit var pickButton: Button

    private lateinit var name: TextView

    //(KS) Reverting loading button
    private val runnableButton = {
        confirmButton.stopAnimation()
        confirmButton.revertAnimation()
    }

    // (SG) Adding meme time utils
    private val current = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        user = (activity as MainActivity).getCurrentUser()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // (MJ Edit) Annoying with swipe feature
        if (!(activity as MainActivity).isValid) {
            //bPickClick()
        }

        // (SG) Find widgets
        val v = inflater.inflate(R.layout.fragment_meme_adding, container, false)
        confirmButton = v.findViewById(R.id.confirmButton) as CircularProgressButton
        pickButton = v.findViewById(R.id.pickButton) as Button
        name = v.findViewById(R.id.name) as TextView
        pic = v.findViewById(R.id.memeImage) as ImageView

        Glide.with(this).load((activity as MainActivity).pic).into(pic)

        setButtons()
        return v
    }

    private fun setButtons() {
        confirmButton.isEnabled = (activity as MainActivity).isValid
        confirmButton.background = this.context!!.getDrawable(R.drawable.button_round2)
        pickButton.background = this.context!!.getDrawable(R.drawable.button_round2)

        //(KS) picking meme when image is clicked
        pic.setOnClickListener { bPickClick() }
        pickButton.setOnClickListener { bPickClick() }

        confirmButton.setOnClickListener {
            confirmButton.startAnimation()
            val path = "memes/" + UUID.randomUUID()
            val memeRef = storage.getReference(path)
            val uploadTask = memeRef.putFile(uri)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation memeRef.downloadUrl
            }).addOnCompleteListener { task ->

                val addTime = current.format(formatter)

                if (task.isSuccessful) {
                    memeUrl = task.result.toString()
                    val newMeme = MemeModel(
                        url = memeUrl,
                        seenBy = arrayListOf(user.uid),
                        rate = 0,
                        location = "location.downloaded.from.phone",
                        addedBy = user.userName,
                        userID = user.uid,
                        dbId = "",
                        addDate = addTime

                    )
                    val meme = HashMap<String, Any>()
                    meme["url"] = memeUrl
                    meme["title"] = name.text.toString()
                    meme["seenBy"] = arrayListOf(user.uid)
                    meme["userId"] = user.uid
                    meme["rate"] = 0
                    meme["location"] = "location.downloaded.from.phone"
                    meme["addedBy"] = user.userName
                    meme["userID"] = user.uid
                    meme["addDate"] = addTime
                    database.collection("Memes").add(meme)
                        .addOnSuccessListener {
                            database.collection("Users").document(user.uid)
                                .update("addedMemes", FieldValue.arrayUnion(it.id))

                            if ((activity as MainActivity).globalUserMemes == null) {
                                (activity as MainActivity).globalUserMemes = ArrayList()
                            }
                            (activity as MainActivity).globalUserMemes!!.add(0, newMeme)

                            (activity as MainActivity).pic =
                                Uri.parse("android.resource://" + this.context!!.packageName + "/" + R.drawable.default_meme_add)
                            (activity as MainActivity).isValid = false
                            setConfirmButton(R.drawable.tick)
                            (activity as MainActivity).nav_view.selectedItemId = R.id.navigation_profile
                            //(activity as MainActivity).displayProfile()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(this.context, "Sorry! Something went wrong :(", Toast.LENGTH_SHORT).show()
                setConfirmButton(R.drawable.cross)
            }
        }
    }

    private fun bPickClick() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "Select picture"), 2233)
    }

    //(KS) set image after loading on button
    private fun setConfirmButton(image: Int) {
        confirmButton.doneLoadingAnimation(Color.parseColor("#FAB162"), BitmapFactory.decodeResource(resources, image))
        handler.postDelayed(runnableButton, 800)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == 2233) {
                uri = data!!.data
                (activity as MainActivity).pic = uri
                (activity as MainActivity).isValid = true
                Glide.with(this).load(uri).into(memeImage)
                memeImage.visibility = View.VISIBLE
                confirmButton.isEnabled = true
            }
        } catch (e: Exception) {
            Log.e("Error", e.message)
        }
    }
}