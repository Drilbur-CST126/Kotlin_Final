package com.jnich.kotlinfinal.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.util.*

data class User (
    val userUuid: String = "",
    val authUid: String,
    val profileName: String,
    val birthDate: Date
): Serializable {
    companion object {
        fun fromReference(userRef: DatabaseReference) : User {
            var user: User? = null
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    user = User("", "", "[Deleted User]", Date())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    user = User(
                        userUuid = snapshot.key ?: "",
                        authUid = snapshot.child("authUid").value as? String ?: "",
                        profileName = snapshot.child("username").value as? String ?: "[Deleted User]",
                        birthDate = snapshot.child("dob").value as? Date ?: Date()
                    )
                }
            })
            return user!!
        }
    }
}