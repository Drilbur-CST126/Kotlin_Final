package com.jnich.kotlinfinal.ui.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.adapter.PostAdapter
import com.jnich.kotlinfinal.controller.Controller
import com.jnich.kotlinfinal.model.Post
import com.jnich.kotlinfinal.model.User
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_USERNAME = "username"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var username: String? = null
    private lateinit var user: User
    private val db = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username = it.getString(ARG_USERNAME)
        }

        //Log.d("ProfileFragment", username)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (username == null || username!!.isEmpty() || username == Controller.user?.profileName) {
            user = Controller.user!!
            initProfile()
        } else {
            val users = db.child("Users")
            users.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    user = User.ERROR_USER
                    users.removeEventListener(this)
                    initProfile()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userChild : DataSnapshot? = snapshot.children.firstOrNull {
                        it.child("username").value == username ?: false
                    }

                    user = if (userChild == null) {
                        User.ERROR_USER
                    } else {
                        User.fromSnapshot(userChild)
                    }
                    users.removeEventListener(this)
                    initProfile()
                }

            })
        }
    }

    private fun initProfile() {
        txt_username.text = user.profileName
        val dob = Calendar.getInstance()
        dob.time = user.birthDate
        txt_dob.text = context?.getString(R.string.txt_dobFormat,
            dob.get(Calendar.MONTH),
            dob.get(Calendar.DAY_OF_MONTH),
            dob.get(Calendar.YEAR))

        val adapter = PostAdapter(requireContext(), ArrayList())
        db.child("Posts").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                adapter.addPost(Post(author = Post.SONGBIRD_AUTHOR, authorUid = "", content = "There has been an error. We apologise for the inconvenience.", likes = 0))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                snapshot.children.forEach {
                    if (it.child("author").value == user.profileName
                        && it.child("replyTo").value == null) {
                        adapter.setPost(Post.fromSnapshot(it), index)
                        index += 1
                    }
                }
            }
        })

        recycler_profile.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler_profile.addItemDecoration(DividerItemDecoration(recycler_profile.context, LinearLayoutManager.VERTICAL))
        recycler_profile.adapter = adapter

        if (!user.authUid.isBlank()) {
            btn_follow.setOnClickListener {
                if (Controller.following?.contains(user.profileName) != true) {
                    db
                        .child("Users")
                        .child(Controller.user!!.authUid)
                        .child("following")
                        .child(user.profileName)
                        .setValue(true)
                    if (Controller.following == null) {
                        Controller.following = ArrayList()
                    }
                    Controller.following!!.add(user.profileName)
                    btn_follow.text = context?.getText(R.string.btn_unfollow)
                } else {
                    db
                        .child("Users")
                        .child(Controller.user!!.authUid)
                        .child("following")
                        .child(user.profileName)
                        .removeValue()
                    Controller.following!!.remove(user.profileName)
                    btn_follow.text = context?.getText(R.string.btn_follow)
                }
                Controller.followingChanged = true
            }
            if (Controller.following?.contains(user.profileName) == true) {
                btn_follow.text = context?.getText(R.string.btn_unfollow)
            }
            btn_follow.isEnabled = (user.authUid != Controller.user?.authUid)
        } else {
            btn_follow.isEnabled = false
        }

        if (user.authUid != Controller.user?.authUid) {
            img_modifyIcon.visibility = View.GONE
        } else {
            img_icon.setOnClickListener {
                val context = requireContext()
                when(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PERMISSION_GRANTED -> {
                        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, READ_FROM_GALLERY)
                    }
                    else -> {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_FROM_GALLERY)
                    }
                }
            }
        }

        if (user.iconUuid != null) {
            val fileRef = storage.child("images").child(user.iconUuid!!)
            val tempFile = File.createTempFile("images", "jpg")
            fileRef.getFile(tempFile).addOnSuccessListener {
                val image = BitmapFactory.decodeFile(tempFile.absolutePath)
                img_icon.setImageBitmap(image)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            READ_FROM_GALLERY -> {
                if (resultCode == RESULT_OK) {
                    val uri = data?.data!!
                    val imageStream = requireContext().contentResolver.openInputStream(uri)
                    val image = BitmapFactory.decodeStream(imageStream)
                    img_icon.setImageBitmap(image)

                    val baos = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val jpgData = baos.toByteArray()

                    val id = "${UUID.randomUUID().toString()}.jpg"
                    storage.child("images").child(id).putBytes(jpgData)
                        .addOnFailureListener {
                            Log.d("ProfileFragment", "Failed to upload image: ${it.message}")
                        }
                    Controller.user?.iconUuid = id
                    db.child("Users").child(Controller.user!!.authUid).child("icon").setValue(id)
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
            READ_FROM_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, READ_FROM_GALLERY)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        const val READ_FROM_GALLERY = 1

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance(param1: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, param1)
                }
            }
    }
}