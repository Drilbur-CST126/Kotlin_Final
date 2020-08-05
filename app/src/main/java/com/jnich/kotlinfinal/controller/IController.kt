package com.jnich.kotlinfinal.controller

import com.jnich.kotlinfinal.model.User
import java.io.Serializable
import java.util.*

interface IController: Serializable {
    var user: User?

    fun verifyAge(date: Date) : Boolean
    fun verifyNewUser(uid: String) : Boolean
    fun publishUserProfile()
}