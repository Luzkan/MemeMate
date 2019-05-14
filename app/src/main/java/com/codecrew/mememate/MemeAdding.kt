package com.codecrew.mememate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_meme_adding.*
import java.util.*
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlin.collections.HashMap

//(PK) Activity for adding new memes

class MemeAdding : AppCompatActivity() {

    lateinit var database :FirebaseFirestore
    lateinit var storage : FirebaseStorage

    lateinit var memeUrl : String
    lateinit var uri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_adding)
        database = FirebaseFirestore.getInstance()
        storage  = FirebaseStorage.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        firestore.firestoreSettings = settings
        confirmButton.isEnabled = false
    }

    //search for meme in a gallery
    fun searchFor(view: View){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "select picture"), 2233 )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try{
            if(resultCode==Activity.RESULT_OK && requestCode == 2233){
                uri = data!!.data
                Glide.with(this).load(uri).into(memeImage)
                memeImage.visibility = View.VISIBLE
                confirmButton.isEnabled = true
            }
        } catch(e:Exception){
            Log.e("blad", e.message)
        }
    }

    //add meme to storage and to database
    fun confirm(view:View){
        if(name.text.isEmpty()){
            Toast.makeText(this, "Name your meme", Toast.LENGTH_SHORT)
            Log.i("pusty", "serio?")
            return
        }
        var path = "memes/"+ UUID.randomUUID()
        var memeRef = storage.getReference(path)
        var uploadTask = memeRef.putFile(uri)
        var url = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if(!task.isSuccessful){
                task.exception?.let{
                    throw it
                }
            }
            return@Continuation memeRef.downloadUrl
        }).addOnCompleteListener{task ->
            if(task.isSuccessful){
                memeUrl = task.result.toString()
                val meme = HashMap<String, Any>()
                meme["url"] = memeUrl
                meme["title"] = name.text.toString()
                meme["seenBy"] = arrayListOf("ten co dodał")
                //meme["userId"] = FirebaseAuth
                database.collection("Memes").add(meme)
                    .addOnSuccessListener {
                        Log.i("dziala???", "chyba nie")
                        database.collection("Users").document("Ginusia").update("addedMemes",FieldValue.arrayUnion("nowyMem") )
                        finish()
                    }
                }
        }


    }
}
