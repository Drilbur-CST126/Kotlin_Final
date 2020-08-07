package com.jnich.kotlinfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_loginSubmit.setOnClickListener {
            val email = edit_loginEmail.text?.toString() ?: ""
            val password = edit_loginPassword.text?.toString() ?: ""
            if (!(email.isBlank() || password.isBlank())) {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d("LoginActivity","Successful login")

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d("LoginActivity","Unsuccessful login")
                    }
            }
        }
    }
}