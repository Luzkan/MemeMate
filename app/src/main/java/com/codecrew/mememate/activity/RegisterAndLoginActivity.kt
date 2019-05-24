package com.codecrew.mememate.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.codecrew.mememate.R
import com.codecrew.mememate.database.models.UserModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*


class RegisterAndLoginActivity : AppCompatActivity() {

    private val handler = Handler()
    private val FB_REQUEST_CODE: Int = 997

    // (KS) Animation after splash screen
    private val runnableSplash = {
        TransitionManager.beginDelayedTransition(lRoot)
        lRegister.visibility = View.VISIBLE
    }

    // (KS) Reverting loading button
    private val runnableButton = {
        bSubmit.stopAnimation()
        bSubmit.revertAnimation()
        bSubmit.background = getDrawable(R.drawable.button_round2)
    }

    // (KS) Launching main application
    @SuppressLint("PrivateResource")
    private val runnableStartApp = {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.abc_grow_fade_in_from_bottom,
            R.anim.abc_shrink_fade_out_from_bottom
        )
    }

    private lateinit var db: FirebaseFirestore

    // (SG) List of account types we can sign in
    private val providers = arrayListOf(
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = FirebaseFirestore.getInstance()

        // (KS) Round login     button
        bSubmit.background = getDrawable(R.drawable.button_round2)

        // (SG) Dynamic enabling submit button
        checkBlank(true)

        // (PR) After log out.
        if (intent.getBooleanExtra("logout", false)) {
            setLoginPanel()
        }

        // (PR) Facebook login fix (facebook_login changed to LoginButton (see activity_register.xml))
        facebook_login.setReadPermissions("email")

        // (KS) Splash screen
        handler.postDelayed(runnableSplash, 1500)
        if (FirebaseAuth.getInstance().currentUser != null) {
            Log.d(
                "USER",
                "${FirebaseAuth.getInstance().currentUser?.displayName} ${FirebaseAuth.getInstance().currentUser?.email} "
            )
            runnableStartApp()
        }
    }

    // (SG) Allows us to open activity to sign in with facebook
    fun showSignOptions(view: View) {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setTheme(
                R.style.AppTheme
            ).build(), FB_REQUEST_CODE
        )
    }

    // (SG) Logs user with facebook
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FB_REQUEST_CODE) {
            // (MJ) Response:
            IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // (MJ) User:
                FirebaseAuth.getInstance().currentUser
                startApp()
            } else {

            }
        }
    }

    fun bSubmitClick(view: View) {
        // (KS) Animated loading button
        bSubmit.startAnimation()

        resetError()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (bSubmit.tag == "signup") {
            val userName = etUsername.text.toString()
            val passwordCheck = etPasswordConfirm.text.toString()

            if (passwordCheck == password) {
                createUser(email, password, userName)
            } else {
                setError("Those passwords didn't match.")
            }
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startApp()
            }.addOnFailureListener {
                setError("Invalid login or password.")
            }
        }
    }

    private fun setError(error: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = error
        setSubmitButton(R.drawable.cross)
    }

    private fun resetError() {
        tvError.text = ""
        tvError.visibility = View.GONE
    }

    // (SG) Dynamic all editText check
    private fun checkBlank(register: Boolean) {

        if (register) {
            checkIfEmpty(etEmail, true)
            checkIfEmpty(etPassword, true)
            checkIfEmpty(etPasswordConfirm, true)
            checkIfEmpty(etUsername, true)
        } else {
            checkIfEmpty(etEmail, false)
            checkIfEmpty(etPassword, false)
        }
    }

    // (SG) Dynamic single editText check
    private fun checkIfEmpty(editable: EditText, register: Boolean) {

        editable.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editable.tag = p0!!.isNotEmpty()
                if (register) {
                    bSubmit.isEnabled =
                        etEmail.tag.toString() == "true" && etPassword.tag.toString() == "true" && etUsername.tag.toString() == "true" && etPasswordConfirm.tag.toString() == "true"
                } else {
                    bSubmit.isEnabled = etEmail.tag.toString() == "true" && etPassword.tag.toString() == "true"
                }

            }
        })
    }

    // (KS) set image after loading on button
    private fun setSubmitButton(image: Int) {
        bSubmit.doneLoadingAnimation(Color.parseColor("#FAB162"), BitmapFactory.decodeResource(resources, image))
        handler.postDelayed(runnableButton, 800)
    }


    @SuppressLint("SetTextI18n")
    private fun createUser(email: String, password: String, userName: String) {

        // (SG) Check if username is taken
        db.document("Users/$userName").get().addOnSuccessListener {

            val user = it.toObject(UserModel::class.java)

            if (user == null) {

                // (SG) Checking if email is taken
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = FirebaseAuth.getInstance().uid ?: ""
                            val newUser = UserModel(uid, email, userName, ArrayList(), ArrayList())

                            FirebaseAuth.getInstance().currentUser!!.updateProfile(
                                UserProfileChangeRequest.Builder().setDisplayName(
                                    userName
                                ).build()
                            )

                            // (SG) Creating a new user in database
                            db.collection("Users").document(FirebaseAuth.getInstance().currentUser!!.uid).set(newUser)
                                .addOnSuccessListener {
                                    startApp()
                                }.addOnFailureListener { exception: java.lang.Exception ->
                                    setError(exception.message + ".")
                                }
                        }
                    }.addOnFailureListener {
                        if (it.message?.length!! > 70) {
                            setError(it.message!!.takeLastWhile { character -> character != '[' }.take(41) + ".")
                        } else {
                            setError(it.message.toString())
                        }
                    }
            } else {
                setError("This username is taken.")
            }
        }
    }

    // (KS) Starting app on logged account
    private fun startApp() {
        setSubmitButton(R.drawable.tick)
        handler.postDelayed(runnableStartApp, 500)
    }

    // (KS) Changing mode login/sign up on textView click
    fun tvChangeClick(view: View) {
        tvError.visibility = View.GONE
        if (bSubmit.tag == "signup") {
            setLoginPanel()
        } else {
            setSignUpPanel()
        }
    }

    // (KS) Setting text on textView and button
    //     and hiding additional fields
    private fun setLoginPanel() {
        tvError.text = ""
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.GONE
        etPasswordConfirm.visibility = View.GONE
        tvChange.text = getString(R.string.signupalter)
        bSubmit.text = getString(R.string.login)
        bSubmit.tag = "login"
        // (SG) Dynamic enabling submit button
        checkBlank(false)
    }

    // (KS) Setting text on textView and button
    //     and adding additional fields
    private fun setSignUpPanel() {
        tvError.text = ""
        TransitionManager.beginDelayedTransition(lRoot)
        etUsername.visibility = View.VISIBLE
        etPasswordConfirm.visibility = View.VISIBLE
        tvChange.text = getString(R.string.loginalter)
        bSubmit.text = getString(R.string.signup)
        bSubmit.tag = "signup"
        // (SG) Dynamic enabling submit button
        checkBlank(true)
    }

    // (KS) Hiding keyboard when click outside the EditText
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
