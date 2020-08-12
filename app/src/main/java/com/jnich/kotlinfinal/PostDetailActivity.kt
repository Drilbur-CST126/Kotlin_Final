package com.jnich.kotlinfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jnich.kotlinfinal.adapter.PostAdapter
import com.jnich.kotlinfinal.model.Post
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.fragment_home.*

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

        val recycler = RecyclerView(this)

        val adapter = PostAdapter(this, ArrayList())
        val posts = db.child("Posts")
        posts.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                posts.removeEventListener(this)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                snapshot.children.forEach {
                    if (it.child("replyTo").value == post.uuid) {
                        adapter.setPost(Post.fromSnapshot(it), index)
                        ++index
                    }
                }
            }

        })

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.addItemDecoration(DividerItemDecoration(recycler.context, LinearLayoutManager.VERTICAL))
        recycler.adapter = adapter
        layout_postDetail.addView(recycler)

        holder.reply.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra("replyName", post.author)
            intent.putExtra("replyPost", post.uuid)
            startActivity(intent)
        }
    }
}