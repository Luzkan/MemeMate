package com.codecrew.mememate

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
    }

    fun bRegisterClick(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        println("REGISTER R")
    }

    fun tvLoginClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        println("LOGIN R")
    }
}