package com.jnich.kotlinfinal.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.model.User

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val username = intent.getStringExtra("username")

        val transaction = supportFragmentManager.beginTransaction()
        val fragment = ProfileFragment.newInstance(username ?: User.ERROR_USER.profileName)
        transaction.add(R.id.layout_profile, fragment, fragment.toString())
        transaction.commit()
    }
}