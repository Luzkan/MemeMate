package com.codecrew.mememate.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.codecrew.mememate.R
import com.codecrew.mememate.activity.login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

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
            finish()
            startActivity(it)
            FirebaseAuth.getInstance().signOut()
        }
    }

    fun changeEmail(view: View) {
        // TODO
    }

    // (PR) Sends an email with password reset link.
    fun passwordReset(view: View) {
        password_reset_button.isEnabled = false
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.email?.also { userEmail ->
            auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()
                        Log.d("SETTINGS", "Email sent.")
                        password_reset_button.isEnabled = true
                    } else {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show()
                        Log.d("SETTINGS", "Email sending error.")
                        password_reset_button.isEnabled = true
                    }
                }
        }
    }
}
