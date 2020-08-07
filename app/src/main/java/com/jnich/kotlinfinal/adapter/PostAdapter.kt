package com.jnich.kotlinfinal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.model.Post

class PostAdapter(private val context: Context, private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorName = view.findViewById<TextView>(R.id.txt_postAuthorName)
        val content = view.findViewById<TextView>(R.id.txt_postContent)
        val likeCount = view.findViewById<TextView>(R.id.txt_postLikeCount)
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
        holder.authorName.text = post.author.profileName
        holder.content.text = post.content
        holder.likeCount.text = post.likes.toString()
    }
}