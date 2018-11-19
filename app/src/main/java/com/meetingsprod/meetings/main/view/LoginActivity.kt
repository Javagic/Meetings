package com.meetingsprod.meetings.main.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.utils.PreferenceManager


class LoginActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val loginBtn by lazy { findViewById<Button>(R.id.loginBtn) }
    private val loginField by lazy { findViewById<TextInputLayout>(R.id.layoutPassword) }
    private val usernameField by lazy { findViewById<TextInputLayout>(R.id.layoutUsername) }
    private val positionField by lazy { findViewById<TextInputLayout>(R.id.layoutPosition) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginBtn.setOnClickListener {
            positionField.editText?.text?.toString()?.let {
                if (it.isNotBlank()) PreferenceManager.saveUserPosition(it)
            }
            usernameField.editText?.text?.toString()?.let {
                if (it.isNotBlank()) {
                    PreferenceManager.saveUserName(it)
                } else {
                    usernameField.error = getString(R.string.error_username)
                    return@setOnClickListener
                }
            }
            mAuth.signInWithEmailAndPassword("mister.rezznik@gmail.com", loginField.editText!!.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
                .addOnFailureListener(this) {
                    loginField.error = when (it) {
                        is FirebaseNetworkException -> getString(R.string.error_connection)
                        else -> getString(R.string.wrong_password)
                    }
                }
        }
    }

    public override fun onStart() {
        super.onStart()
//        mAuth.createToken()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = mAuth.getCurrentUser()
//        updateUI(currentUser)
    }


}