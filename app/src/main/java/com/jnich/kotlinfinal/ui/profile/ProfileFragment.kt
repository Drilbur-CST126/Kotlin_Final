package com.jnich.kotlinfinal.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
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
import com.jnich.kotlinfinal.model.User
import kotlinx.android.synthetic.main.fragment_profile.*

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
            db.child("Users").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    user = User.ERROR_USER
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

                    initProfile()
                }

            })
        }
    }

    private fun initProfile() {
        txt_username.text = user.profileName
        txt_dob.text = user.birthDate.toString()

        val adapter = PostAdapter(requireContext(), ArrayList())
        db.child("Posts").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                adapter.addPost(Post(author = Post.SONGBIRD_AUTHOR, authorUid = "", content = "There has been an error. We apologise for the inconvenience.", likes = 0))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()
                snapshot.children.forEach {
                    if (it.child("author").value == user.profileName) {
                        adapter.addPost(Post.fromSnapshot(it))
                    }
                }
            }
        })

        recycler_profile.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler_profile.addItemDecoration(DividerItemDecoration(recycler_profile.context, LinearLayoutManager.VERTICAL))
        recycler_profile.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
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