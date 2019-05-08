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
    }

    fun bSubmitClick(view: View) {
        if (bSubmit.tag == "register") {
            val userName = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            createUser(email, password, userName)
        } else {
            startApp()
        }
    }


    private fun createUser(email: String, password: String, userName: String) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // If empty login in db here is the problem
                val uid = FirebaseAuth.getInstance().uid ?: ""
                val newUser = UserModel(uid, email, userName)

                db.collection("Users").document(newUser.uid).set(newUser).addOnSuccessListener { void: Void? ->
                    Log.d("SAVE", "SUCCESS")
                }.addOnFailureListener { exception: java.lang.Exception ->
                    Log.d("SAVE", "ERROR")
                }
            }
        }.addOnFailureListener {
            Log.d("LOGIN", "FAIL")
        }
    }

    private fun startApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun tvChangeClick(view: View) {
        if (bSubmit.tag == "register") {
            setLoginPanel()
        } else {
            setRegisterPanel()
        }
    }

    private fun setLoginPanel() {
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.GONE
        etPasswordConfirm.visibility = View.GONE
        tvChange.text = "New user? Sign up here!"
        bSubmit.text = "LOG IN"
        bSubmit.tag = "login"
    }

    private fun setRegisterPanel() {
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.VISIBLE
        etPasswordConfirm.visibility = View.VISIBLE
        tvChange.text = "Already have an account?"
        bSubmit.text = "REGISTER"
        bSubmit.tag = "register"
    }

}