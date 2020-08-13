package com.jnich.kotlinfinal.ui.likes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.adapter.PostAdapter
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.model.Post
import kotlinx.android.synthetic.main.fragment_likes.*

class LikesFragment : Fragment() {
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_likes, container, false)
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
                adapter.clear()
                Controller.user?.likes?.forEach {
                    adapter.addPost(Post.fromSnapshot(snapshot.child(it)))
                }
                //posts.removeEventListener(this)
            }
        })

        recycler_likes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler_likes.addItemDecoration(DividerItemDecoration(recycler_likes.context, LinearLayoutManager.VERTICAL))
        recycler_likes.adapter = adapter
    }
}