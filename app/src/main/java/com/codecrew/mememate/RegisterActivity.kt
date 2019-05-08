package com.codecrew.mememate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.View
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val handler = Handler()

    private val runnable = {
        TransitionManager.beginDelayedTransition(lRoot)
        lRegister.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        val email = etEmail.text.toString()
//        val password = etPassword.text.toString()

        handler.postDelayed(runnable, 1500)
    }

    fun bSubmitClick(view: View) {
        if (bSubmit.tag == "register") {
            startApp()
        } else {
            startApp()
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