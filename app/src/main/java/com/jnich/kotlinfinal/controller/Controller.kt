package com.jnich.kotlinfinal.controller

import com.jnich.kotlinfinal.model.User
import java.util.*

object Controller {
    var user: User? = null
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

    fun verifyAge(date: Date): Boolean {
        return getAge(date) >= 13
    }
}