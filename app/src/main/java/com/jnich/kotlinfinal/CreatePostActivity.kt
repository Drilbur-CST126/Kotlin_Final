package com.jnich.kotlinfinal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.ui.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.ByteArrayOutputStream
import java.util.*

class CreatePostActivity : AppCompatActivity() {
    private var replyPost: String? = null
    private val db = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference
    private var imageId: String? = null
    private var imageData: ByteArray? = null

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

                    if (imageId != null) {
                        storage.child("images").child(imageId!!).putBytes(imageData!!)
                            .addOnFailureListener {
                                Log.d("ProfileFragment", "Failed to upload image: ${it.message}")
                            }
                        post.child("imageUuid").setValue(imageId)
                    }

                    finish()
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_noContentError), Toast.LENGTH_SHORT).show()
            }
        }

        btn_addImage.setOnClickListener {
            when(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                PackageManager.PERMISSION_GRANTED -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, ProfileFragment.READ_FROM_GALLERY)
                }
                else -> {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        ProfileFragment.READ_FROM_GALLERY
                    )
                }
            }
        }

        img_createPost.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ProfileFragment.READ_FROM_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.data!!
                    val imageStream = contentResolver.openInputStream(uri)
                    val image = BitmapFactory.decodeStream(imageStream)
                    img_createPost.setImageBitmap(image)
                    img_createPost.visibility = View.VISIBLE

                    val baos = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val jpgData = baos.toByteArray()

                    imageId = "${UUID.randomUUID()}.jpg"
                    imageData = jpgData
                    imageStream?.close()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ProfileFragment.READ_FROM_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, ProfileFragment.READ_FROM_GALLERY)
                }
            }
        }
    }


}