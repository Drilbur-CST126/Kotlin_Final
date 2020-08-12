package com.jnich.kotlinfinal.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.jnich.kotlinfinal.CreatePostActivity
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.adapter.PostAdapter
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.model.Post
import com.jnich.kotlinfinal.model.User
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val list = ArrayList<Post>()
        val adapter = PostAdapter(requireContext(), list)
        val posts = db.child("Posts")
        posts.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                adapter.addPost(Post(author = Post.SONGBIRD_AUTHOR, authorUid = "", content = "There has been an error. We apologise for the inconvenience.", likes = 0))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (Controller.followingChanged) {
                    adapter.clear()
                    Controller.followingChanged = false
                }
                var index = 0
                snapshot.children.forEach {
                    if (Controller.following == null || Controller.following?.size == 0 || Controller.following?.contains(it.child("author").value) != false) {
                        adapter.setPost(Post.fromSnapshot(it), index)
                        index += 1
                    }
                }
            }
        })

        recycler_home.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler_home.addItemDecoration(DividerItemDecoration(recycler_home.context, LinearLayoutManager.VERTICAL))
        recycler_home.adapter = adapter

        fab_home.setOnClickListener {
            val intent = Intent(context, CreatePostActivity::class.java)
            context?.startActivity(intent)
        }
    }
}