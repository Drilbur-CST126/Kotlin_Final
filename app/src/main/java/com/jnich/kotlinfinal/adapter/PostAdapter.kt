package com.jnich.kotlinfinal.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jnich.kotlinfinal.PostDetailActivity
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.model.Post
import com.jnich.kotlinfinal.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File

class PostAdapter(private val context: Context, private val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private val db = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorName = view.findViewById<TextView>(R.id.txt_postAuthorName)!!
        val content = view.findViewById<TextView>(R.id.txt_postContent)!!
        val likeCount = view.findViewById<TextView>(R.id.txt_postLikeCount)!!
        val heart = view.findViewById<ToggleButton>(R.id.tgl_heart)!!
        val layout = view.findViewById<ConstraintLayout>(R.id.layout_post)!!
        val reply = view.findViewById<Button>(R.id.btn_reply)!!
        val image = view.findViewById<ImageView>(R.id.img_post)!!
        var id = ""

        val liked: Boolean
            get() = Controller.user?.likes?.contains(id) ?: false
        
        fun bind(context: Context, post: Post, db: DatabaseReference, storage: StorageReference, focus: Boolean = false) {
            authorName.text = post.author
            content.text = post.content
            likeCount.text = post.likes.toString()
            id = post.uuid

            if (focus) {
                authorName.setTextAppearance(R.style.TextAppearance_AppCompat_Large)
            } else {
                reply.visibility = View.GONE
            }

            heart.isChecked = liked
            heart.setOnClickListener {
                if (liked) {
                    Controller.user?.likes?.remove(id)
                    post.likes -= 1
                    db
                        .child("Users")
                        .child(Controller.user!!.authUid)
                        .child("likes")
                        .child(id)
                        .removeValue()
                    //heart.setImageDrawable(context.getDrawable(R.drawable.ic_heart))
                } else {
                    Controller.user?.likes?.add(id)
                    post.likes += 1
                    db
                        .child("Users")
                        .child(Controller.user!!.authUid)
                        .child("likes")
                        .child(id).setValue(true) // Dummy value
                    //heart.setImageDrawable(context.getDrawable(R.drawable.ic_filled_heart))
                }
                db
                    .child("Posts")
                    .child(id)
                    .child("likes")
                    .setValue(post.likes)
                likeCount.text = post.likes.toString()
            }

            authorName.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("username", post.author)
                context.startActivity(intent)
            }

            if (post.imageUuid != null) {
                val fileRef = storage.child("images").child(post.imageUuid!!)
                val tempFile = File.createTempFile("images", "jpg")
                fileRef.getFile(tempFile).addOnSuccessListener {
                    val imageBmp = BitmapFactory.decodeFile(tempFile.absolutePath)
                    image.setImageBitmap(imageBmp)
                }
            } else {
                image.visibility = View.GONE
            }
        }
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
        holder.bind(context, post, db, storage)

        holder.layout.setOnClickListener {
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra("post", post)
            context.startActivity(intent)
        }
    }

    fun setPost(post: Post, index: Int) {
        if (index >= posts.size) {
            addPost(post)
        } else if (post != posts[index]) {
            posts[index] = post
            notifyItemChanged(index)
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