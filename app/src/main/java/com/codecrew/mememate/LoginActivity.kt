package com.codecrew.mememate

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        val email = etEmail.text.toString()
//        val password = etPassword.text.toString()
    }

    fun bLoginClick(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        println("LOG IN L")
    }

    fun tvRegisterClick(view: View) {
        super.onBackPressed()

        println("LOGIN L")
    }
}