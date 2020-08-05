package com.jnich.kotlinfinal

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.controller.IController
import kotlinx.android.synthetic.main.activity_startup.*

class StartupActivity : AppCompatActivity() {
    private val controller: IController = Controller()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        btn_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("CONTROLLER", controller)
            startActivity(intent)
        }

        btn_signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("CONTROLLER", controller)
            startActivity(intent)
        }
    }
}