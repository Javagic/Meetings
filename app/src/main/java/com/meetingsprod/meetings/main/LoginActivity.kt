package com.meetingsprod.meetings.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.meetingsprod.meetings.R


class LoginActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val loginBtn by lazy { findViewById<Button>(R.id.loginBtn) }
    private val loginField by lazy { findViewById<TextInputLayout>(R.id.layoutPassword) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginBtn.setOnClickListener {
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
