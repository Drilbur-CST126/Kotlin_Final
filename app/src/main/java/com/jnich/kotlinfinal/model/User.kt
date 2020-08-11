package com.jnich.kotlinfinal.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable
import java.util.*

data class User (
    val authUid: String,
    val profileName: String,
    val birthDate: Date,
    val likes: MutableSet<String>? = null
): Serializable {
    companion object {
        val ERROR_USER = User("", "[Unknown User]", Date())

        fun fromSnapshot(snapshot: DataSnapshot) : User {
            return User(
                authUid = snapshot.key ?: ERROR_USER.authUid,
                profileName = snapshot.child("username").value as? String ?: ERROR_USER.profileName,
                birthDate = snapshot.child("dob").value as? Date ?: ERROR_USER.birthDate,
                likes = snapshot.child("likes").children.map {
                    it.key!!
                }.toMutableSet()
            )
        }
    }
}