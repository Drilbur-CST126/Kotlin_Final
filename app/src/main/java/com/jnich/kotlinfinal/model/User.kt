package com.jnich.kotlinfinal.model

import com.google.firebase.auth.FirebaseUser
import java.io.Serializable
import java.util.*

data class User (
    val uuid: String = "",
    val fbUserId: String,
    val profileName: String,
    val birthDate: Date
): Serializable