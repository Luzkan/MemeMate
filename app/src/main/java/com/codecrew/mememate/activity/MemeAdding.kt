package com.codecrew.mememate.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.codecrew.mememate.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_meme_adding.*
import java.util.*
import kotlin.collections.HashMap

// (PK) Activity for adding new memes
abstract class MemeAdding : AppCompatActivity() {

    lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var memeUrl: String
    private lateinit var uri: Uri
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_adding)
        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        confirmButton.isEnabled = false
    }

    // Search for a meme in a gallery
    fun searchFor(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2233)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == 2233) {
                uri = data!!.data
                Glide.with(this).load(uri).into(memeImage)
                memeImage.visibility = View.VISIBLE
                confirmButton.isEnabled = true
            }
        } catch (e: Exception) {
            Log.e("Error", e.message)
        }
    }

    // Add meme to storage and to database
    fun confirm(view: View) {
        if (name.text.isEmpty()) {
            Toast.makeText(this, "Name Your Meme", Toast.LENGTH_SHORT).show()
            return
        }
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
            if (task.isSuccessful) {
                memeUrl = task.result.toString()
                val meme = HashMap<String, Any>()
                meme["url"] = memeUrl
                meme["title"] = name.text.toString()
                meme["seenBy"] = arrayListOf(user.uid)
                meme["userId"] = user.uid
                meme["rate"] = 0
                meme["location"] = "location.downloaded.from.phone"
                database.collection("Memes").add(meme)
                    .addOnSuccessListener {
                        database.collection("Users").document(user.uid)
                            .update("addedMemes", FieldValue.arrayUnion(it.id))
                    }
            }
        }
    }
}
