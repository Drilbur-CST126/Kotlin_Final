package com.jnich.kotlinfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import com.jnich.kotlinfinal.controller.Controller
import kotlinx.android.synthetic.main.activity_create_post.*
import java.util.*

class CreatePostActivity : AppCompatActivity() {
    private var replyPost: String? = null
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val replyUsername = intent.getStringExtra("replyUsername")
        replyPost = intent.getStringExtra("replyPost")

        if (replyUsername != null) {
            txt_replyingTo.text = replyUsername
        } else {
            txt_replyingTo.visibility = View.GONE
        }

        btn_post.setOnClickListener {
            if (!edit_content.text.isBlank()) {
                val posts = db.child("Posts")
                val key = posts.push().key
                if (key != null) {
                    val post = posts.child(key)
                    post.child("content").setValue(edit_content.text.toString())
                    post.child("author").setValue(Controller.user?.profileName)
                    post.child("authorUid").setValue(Controller.user?.authUid)
                    post.child("likes").setValue(0)
                    post.child("uuid").setValue(key)
                    post.child("replyTo").setValue(replyPost)

                    finish()
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_noContentError), Toast.LENGTH_SHORT).show()
            }
        }
    }
}