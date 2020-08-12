package com.jnich.kotlinfinal.controller

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import com.jnich.kotlinfinal.R
import com.jnich.kotlinfinal.model.User
import java.util.*

object Controller {
    var user: User? = null
    var following: MutableList<String>? = null
    var followingChanged = false
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