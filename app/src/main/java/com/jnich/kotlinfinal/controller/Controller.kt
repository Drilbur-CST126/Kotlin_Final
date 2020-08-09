package com.jnich.kotlinfinal.controller

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jnich.kotlinfinal.model.User
import java.io.Serializable
import java.security.SecureRandom
import java.util.*

object Controller: IController {
    override var user: User? = null
    //var db: DatabaseReference = FirebaseDatabase.getInstance().reference

    private fun getAge(date: Date) : Int {
        val today = Calendar.getInstance()
        val dob = Calendar.getInstance()
        dob.time = date
        today.add(Calendar.DAY_OF_MONTH, -dob.get(Calendar.DAY_OF_MONTH) + 1)
        today.add(Calendar.MONTH, -dob.get(Calendar.MONTH))
        today.add(Calendar.YEAR, -dob.get(Calendar.YEAR))
        return today.get(Calendar.YEAR)
    }

    override fun verifyAge(date: Date): Boolean {
        return getAge(date) >= 13
    }

    override fun verifyNewUser(uid: String): Boolean {
        TODO("Ugh")
    }

    override fun publishUserProfile() {
        TODO("Ugh")
    }
}