package com.jnich.kotlinfinal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.model.Post

class PostAdapter(private val context: Context, private val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private val db = FirebaseDatabase.getInstance().reference

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorName = view.findViewById<TextView>(R.id.txt_postAuthorName)!!
        val content = view.findViewById<TextView>(R.id.txt_postContent)!!
        val likeCount = view.findViewById<TextView>(R.id.txt_postLikeCount)!!
        val heart = view.findViewById<ToggleButton>(R.id.tgl_heart)!!
        var id = ""

        val liked: Boolean
            get() = Controller.user?.likes?.contains(id) ?: false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context!!)
            .inflate(R.layout.card_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.authorName.text = post.author
        holder.content.text = post.content
        holder.likeCount.text = post.likes.toString()
        holder.id = post.uuid

        holder.heart.isChecked = holder.liked
        holder.heart.setOnClickListener {
            if (holder.liked) {
                Controller.user?.likes?.remove(holder.id)
                post.likes -= 1
                db
                    .child("Users")
                    .child(Controller.user!!.authUid)
                    .child("likes")
                    .child(holder.id)
                    .removeValue()
                //holder.heart.setImageDrawable(context.getDrawable(R.drawable.ic_heart))
            } else {
                Controller.user?.likes?.add(holder.id)
                post.likes += 1
                db
                    .child("Users")
                    .child(Controller.user!!.authUid)
                    .child("likes")
                    .child(holder.id).setValue(true) // Dummy value
                //holder.heart.setImageDrawable(context.getDrawable(R.drawable.ic_filled_heart))
            }
            db
                .child("Posts")
                .child(holder.id)
                .child("likes")
                .setValue(post.likes)
            holder.likeCount.text = post.likes.toString()
        }
    }

    fun addPost(post: Post) {
        posts.add(post)
        notifyItemInserted(posts.size)
    }

    fun clear() {
        val size = posts.size
        posts.clear()
        notifyItemRangeRemoved(0, size)
    }
}