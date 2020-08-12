package com.jnich.kotlinfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.jnich.kotlinfinal.adapter.PostAdapter
import com.jnich.kotlinfinal.model.Post
import kotlinx.android.synthetic.main.activity_post_detail.*

class PostDetailActivity : AppCompatActivity() {
    private lateinit var holder: PostAdapter.ViewHolder
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val post = intent.getSerializableExtra("post") as Post
        val postCard = LayoutInflater.from(this)
            .inflate(R.layout.card_post, layout_postDetail, false)
        holder = PostAdapter.ViewHolder(postCard)
        holder.bind(this, post, db, true)
        layout_postDetail.addView(postCard)

        val repliesText = TextView(this)
        repliesText.text = getString(R.string.txt_replies)
        layout_postDetail.addView(repliesText)
    }
}