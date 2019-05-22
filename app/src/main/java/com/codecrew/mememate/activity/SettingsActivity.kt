package com.codecrew.mememate.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    // (PR) log_out button functionality.
    fun logout(view: View) {
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
        Intent(this, RegisterActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            it.putExtra("logout", true)
            startActivity(it)
            FirebaseAuth.getInstance().signOut()
        }
    }
}
