package com.jnich.kotlinfinal.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.adapter.PostAdapter
import com.jnich.kotlinfinal.model.Post
import com.jnich.kotlinfinal.model.User
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val user1 = User("", "", "Jordan1", Date())
        val user2 = User("", "", "Jordan2", Date())
        val list = arrayListOf(
            Post(user1, "Content", null, 420),
            Post(user2, "Jontent", null, 2)
        )

        recycler_home.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val adapter = PostAdapter(requireContext(), list)
        recycler_home.adapter = adapter
    }
}