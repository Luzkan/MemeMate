package com.codecrew.mememate.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.MainActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_meme_adding.*
import kotlinx.android.synthetic.main.fragment_meme_adding.view.*
import java.util.*
import kotlin.collections.HashMap

class AddMemeFragment : Fragment() {

    private lateinit var database : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    private lateinit var pic : ImageView

    private lateinit var memeUrl : String
    private lateinit var uri : Uri

    private lateinit var user : FirebaseUser

    private lateinit var confirmButton : Button
    private lateinit var pickButton : Button

    private lateinit var name : TextView

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
        pickButton = v.findViewById(R.id.pickButton) as Button
        name = v.findViewById(R.id.name) as TextView
        pic = v.findViewById(R.id.memeImage) as ImageView

        Glide.with(this).load((activity as MainActivity).pic).into(pic)

        setButtons()
        return v
    }

    private fun setButtons(){
//        confirmButton.isEnabled = false
        confirmButton.isEnabled = (activity as MainActivity).isValid

//        searchButton.setOnClickListener{
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent, "select picture"), 2233 )
//        }

        pickButton.setOnClickListener{bPickClick()}

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
                    meme["addedBy"] = user.displayName.toString()
                    database.collection("Memes").add(meme)
                        .addOnSuccessListener {
                            database.collection("Users").document(user.uid).update("addedMemes", FieldValue.arrayUnion(it.id) )
                        }
                    (activity as MainActivity).nav_view.selectedItemId = R.id.navigation_profile
                    (activity as MainActivity).displayProfile()
                }
            }
        }
    }

    private fun bPickClick() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "select picture"), 2233 )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try{
            if(resultCode== Activity.RESULT_OK && requestCode == 2233){
                uri = data!!.data
                (activity as MainActivity).pic = uri
                (activity as MainActivity).isValid = true
                Glide.with(this).load(uri).into(memeImage)
                memeImage.visibility = View.VISIBLE
                confirmButton.isEnabled = true
            }
        } catch(e:Exception){
            Log.e("blad", e.message)
        }
    }

}