package com.codecrew.mememate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import com.codecrew.mememate.database.UserModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager

//todo zablokować przycisk jak pola są puste

//todo po zalogowaniu czyścić pola

//todo obsłużyć blanka

class RegisterActivity : AppCompatActivity() {

    private val handler = Handler()
    private val FB_REQUEST_CODE : Int = 997

    //(KS) Animation after splash screen
    private val runnableSplash = {
        TransitionManager.beginDelayedTransition(lRoot)
        lRegister.visibility = View.VISIBLE
    }

    //(KS) Reverting loading button
    private val runnableButton = {
        bSubmit.stopAnimation()
        bSubmit.revertAnimation()
        bSubmit.background = getDrawable(R.drawable.button_round2)
    }

    //(KS) Launching main application
    @SuppressLint("PrivateResource")
    private val runnableStartApp = {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
    }

    lateinit var db: FirebaseFirestore

    val providers = arrayListOf(
            AuthUI.IdpConfig.FacebookBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = FirebaseFirestore.getInstance()

        //(KS) Splash screen
        handler.postDelayed(runnableSplash, 1500)
        if(FirebaseAuth.getInstance().currentUser != null){
            Log.d("USER", "${FirebaseAuth.getInstance().currentUser?.displayName} ${FirebaseAuth.getInstance().currentUser?.email} ")
            runnableStartApp()
        }
    }

    fun showSignOptions(view: View){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setTheme(R.style.AppTheme).build(),FB_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == FB_REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                startApp()
            } else {

                Log.d("XDD","$resultCode")
            }
        }
    }

    fun bSubmitClick(view: View) {
//        (KS) Animated loading button
        bSubmit.startAnimation()

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
                setSubmitButton(R.drawable.cross)
            }
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener {
                startApp()
            }.addOnFailureListener{
                tvError.text = "Invalid login or password."
                setSubmitButton(R.drawable.cross)
            }
        }
    }

    //(KS) set image after loading on button
    private fun setSubmitButton(image: Int) {
        bSubmit.doneLoadingAnimation(Color.parseColor("#FAB162"), BitmapFactory.decodeResource(resources, image))
        handler.postDelayed(runnableButton, 800)
    }


    @SuppressLint("SetTextI18n")
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
                                    startApp()
                                }.addOnFailureListener { exception: java.lang.Exception ->
                                    tvError.text = exception.message + "."
                                    setSubmitButton(R.drawable.cross)
                                }
                        }
                    }.addOnFailureListener {
                        if (it.message?.length!! > 70) {
                            tvError.text = it.message!!.takeLastWhile { character -> character != '[' }.take(41) + "."
                            setSubmitButton(R.drawable.cross)
                        } else {
                            tvError.text = it.message
                            setSubmitButton(R.drawable.cross)
                        }
                    }
            } else {
                tvError.text = "This username is taken."
                setSubmitButton(R.drawable.cross)
            }
        }
    }

    //(KS) Starting app on logged account
    private fun startApp() {
        setSubmitButton(R.drawable.tick)
        handler.postDelayed(runnableStartApp, 500)
    }

    //(KS) Changing mode login/sign up on textView click
    fun tvChangeClick(view: View) {
        if (bSubmit.tag == "signup") {
            setLoginPanel()
        } else {
            setSignUpPanel()
        }
    }

    //(KS) Setting text on textView and button
    //     and hiding additional fields
    private fun setLoginPanel() {
        tvError.text = ""
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.GONE
        etPasswordConfirm.visibility = View.GONE
        tvChange.text = "New user? Sign up here!"
        bSubmit.text = "LOG IN"
        bSubmit.tag = "login"
    }

    //(KS) Setting text on textView and button
    //     and adding additional fields
    private fun setSignUpPanel() {
        tvError.text = ""
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.VISIBLE
        etPasswordConfirm.visibility = View.VISIBLE
        tvChange.text = "Already have an account?"
        bSubmit.text = "SIGN UP"
        bSubmit.tag = "signup"
    }

    //(KS) Hiding keyboard when click outside the EditText
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

}