package com.jnich.kotlinfinal.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import java.io.Serializable
import java.util.*

data class Post(
    var author: String,
    var authorUid: String,
    var content: String,
    var imageUrl: String? = null,
    var likes: Int,
    var uuid: String = ""
): Serializable {
    companion object {
        const val SONGBIRD_AUTHOR = "Songbird (Automated)"

        fun fromSnapshot(snapshot: DataSnapshot) : Post {
            return Post(
                author = snapshot.child("author").value as? String ?: "[Unknown User]",
                authorUid = snapshot.child("authorUid").value as? String ?: "",
                content = snapshot.child("content").value as? String ?: "",
                imageUrl = snapshot.child("imageUrl").value as? String,
                likes = (snapshot.child("likes").value as? Long)?.toInt() ?: 0,
                uuid = snapshot.child("uuid").value as? String ?: ""
            )
        }
    }
}