package com.jnich.kotlinfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val users = FirebaseDatabase.getInstance().reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_loginSubmit.setOnClickListener {
            val email = edit_loginEmail.text?.toString() ?: ""
            val password = edit_loginPassword.text?.toString() ?: ""
            if (!(email.isBlank() || password.isBlank())) {
                val intent = Intent(this, MainActivity::class.java)
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d("LoginActivity","Successful login")

                        users.child(it.user!!.uid).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                Controller.user = User.ERROR_USER
                                startActivity(intent)
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                Controller.user = User.fromSnapshot(snapshot)
                                Log.d("LoginActivity","User set successfully! ${Controller.user.toString()}")
                                startActivity(intent)
                            }

                        })
                    }
                    .addOnFailureListener {
                        Log.d("LoginActivity","Unsuccessful login")
                    }
            }
        }
    }
}