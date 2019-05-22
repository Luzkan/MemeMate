package com.codecrew.mememate.activity

import android.app.AlertDialog
import android.content.DialogInterface
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

    fun deleteUser(view: View) {
        delete_account_button.isEnabled = false

        val alertDialog = this.let { settingsActivity ->
            val builder = AlertDialog.Builder(settingsActivity)
            builder.apply {
                setPositiveButton(R.string.yes) { dialog, id ->
                    // User clicked Yes button
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("SETTINGS", "User account deleted.")
                                Toast.makeText(this.context, "User account deleted.", Toast.LENGTH_SHORT).show()
                                Intent(this.context, RegisterActivity::class.java).also {
                                    it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    startActivity(it)
                                }
                            } else {
                                Log.d("SETTINGS", "User account deletion error.")
                                Toast.makeText(this.context, "Something went wrong.", Toast.LENGTH_SHORT).show()
                                delete_account_button.isEnabled = true
                            }
                        }
                }
                setNegativeButton(R.string.no) { dialog, id ->
                    // User cancelled the dialog
                    delete_account_button.isEnabled = true
                }
            }
            // Set other dialog properties
            builder.setMessage("Are you sure? This operation cannot be rewind!")
            builder.setTitle("Delete my account")

            // Create the AlertDialog
            builder.create()
        }

        alertDialog.show()

    }

}
