package com.codecrew.mememate.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

class AddMemeFragment : Fragment() {

    lateinit var database : FirebaseFirestore
    lateinit var storage : FirebaseStorage

    lateinit var memeUrl : String
    lateinit var uri : Uri

    lateinit var user : FirebaseUser

    lateinit var confirmButton : Button
    lateinit var searchButton : Button

    lateinit var name : TextView

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseFirestore.getInstance()
        storage  = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // (SG) Find widgets
        val v = inflater.inflate(R.layout.fragment_meme_adding, container, false)
        confirmButton = v.findViewById(R.id.confirmButton) as Button
        searchButton = v.findViewById(R.id.searchButton) as Button
        name = v.findViewById(R.id.name) as TextView

        setButtons()
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


    fun setButtons(){
        confirmButton.isEnabled = false

        searchButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "select picture"), 2233 )
        }
        confirmButton.setOnClickListener{
            if(name.text.isEmpty()){
                Toast.makeText(this.context, "Name your meme", Toast.LENGTH_SHORT).show()
            }
            val path = "memes/"+ UUID.randomUUID()
            val memeRef = storage.getReference(path)
            val uploadTask = memeRef.putFile(uri)
            var url = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }
                return@Continuation memeRef.downloadUrl
            }).addOnCompleteListener{ task ->
                if(task.isSuccessful){
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
                            database.collection("Users").document(user.uid).update("addedMemes", FieldValue.arrayUnion(it.id) )
                        }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try{
            if(resultCode== Activity.RESULT_OK && requestCode == 2233){
                uri = data!!.data
                Glide.with(this).load(uri).into(memeImage)
                memeImage.visibility = View.VISIBLE
                confirmButton.isEnabled = true
            }
        } catch(e:Exception){
            Log.e("blad", e.message)
        }
    }
}