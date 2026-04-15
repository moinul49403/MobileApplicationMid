package com.example.loginprofileapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtForgot: TextView
    private lateinit var btnLogin: Button
    private lateinit var profileCard: RelativeLayout
    private lateinit var btnLogout: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        txtForgot = findViewById(R.id.txtForgot)
        btnLogin = findViewById(R.id.btnLogin)
        profileCard = findViewById(R.id.profileCard)
        btnLogout = findViewById(R.id.btnLogout)
        progressBar = findViewById(R.id.progressBar)

        txtForgot.setOnClickListener {
            Toast.makeText(
                this,
                "Password reset link sent to your email",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username == "admin" && password == "1234") {
                progressBar.visibility = View.VISIBLE
                btnLogin.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.visibility = View.GONE

                    // Hide login form
                    findViewById<View>(R.id.imgLogo).visibility = View.GONE
                    findViewById<View>(R.id.txtTitle).visibility = View.GONE
                    etUsername.visibility = View.GONE
                    etPassword.visibility = View.GONE
                    txtForgot.visibility = View.GONE
                    btnLogin.visibility = View.GONE

                    // Show profile card
                    profileCard.visibility = View.VISIBLE
                }, 1500)
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            profileCard.visibility = View.GONE

            findViewById<View>(R.id.imgLogo).visibility = View.VISIBLE
            findViewById<View>(R.id.txtTitle).visibility = View.VISIBLE
            etUsername.visibility = View.VISIBLE
            etPassword.visibility = View.VISIBLE
            txtForgot.visibility = View.VISIBLE
            btnLogin.visibility = View.VISIBLE
            btnLogin.isEnabled = true

            etUsername.text.clear()
            etPassword.text.clear()
        }
    }
}