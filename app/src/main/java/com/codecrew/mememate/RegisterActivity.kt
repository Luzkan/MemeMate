package com.codecrew.mememate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.codecrew.mememate.database.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import java.net.Authenticator

class RegisterActivity : AppCompatActivity() {

    private val handler = Handler()

    private val runnable = {
        TransitionManager.beginDelayedTransition(lRoot)
        lRegister.visibility = View.VISIBLE
    }

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = FirebaseFirestore.getInstance()
        handler.postDelayed(runnable, 1500)
        if(FirebaseAuth.getInstance().currentUser != null){
            Log.d("USER", "${FirebaseAuth.getInstance().currentUser?.displayName} ${FirebaseAuth.getInstance().currentUser?.email} ")
            startApp()
        }
    }

    fun bSubmitClick(view: View) {

        tvError.text = ""
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        if (bSubmit.tag == "signup") {
            val userName = etUsername.text.toString()
            val passwordCheck = etPasswordConfirm.text.toString()

            if(passwordCheck == password){
                createUser(email, password, userName)
            } else {
                tvError.text = "Those passwords didn't match."
            }
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener {
                startApp()
            }.addOnFailureListener{
               tvError.text = "Invalid login or password."
            }
        }
    }


    private fun createUser(email: String, password: String, userName: String) {

        //(SG) Check if username is taken
        db.document("Users/$userName").get().addOnSuccessListener {

            val user = it.toObject(UserModel::class.java)

            if (user == null) {

                //(SG) Checking if email is taken
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = FirebaseAuth.getInstance().uid ?: ""
                            val newUser = UserModel(uid, email, userName)

                            // (SG) Creating a new user in database
                            db.collection("Users").document(newUser.userName).set(newUser)
                                .addOnSuccessListener { void: Void? ->
                                    // Loading circle
                                    startApp()
                                }.addOnFailureListener { exception: java.lang.Exception ->
                                    tvError.text = exception.message + "."
                                }
                        }
                    }.addOnFailureListener {
                    if (it.message?.length!! > 70) {
                        tvError.text = it.message!!.takeLastWhile { character -> character != '[' }.take(41) + "."
                    } else {
                        tvError.text = it.message
                    }
                }
            } else {
                tvError.text = "This username is taken."
            }
        }
    }


    private fun startApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun tvChangeClick(view: View) {
        if (bSubmit.tag == "signup") {
            setLoginPanel()
        } else {
            setSignUpPanel()
        }
    }

    private fun setLoginPanel() {
        tvError.text = ""
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.GONE
        etPasswordConfirm.visibility = View.GONE
        tvChange.text = "New user? Sign up here!"
        bSubmit.text = "LOG IN"
        bSubmit.tag = "login"
    }

    private fun setSignUpPanel() {
        tvError.text = ""
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.VISIBLE
        etPasswordConfirm.visibility = View.VISIBLE
        tvChange.text = "Already have an account?"
        bSubmit.text = "SIGN UP"
        bSubmit.tag = "signup"
    }

}