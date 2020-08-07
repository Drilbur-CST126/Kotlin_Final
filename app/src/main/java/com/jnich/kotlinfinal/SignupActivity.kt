package com.jnich.kotlinfinal

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jnich.kotlinfinal.controller.IController
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity: AppCompatActivity() {
    private lateinit var controller: IController
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var dbRef: DatabaseReference

    private var email = ""
    private var date: Date? = null
    private var username = ""
    private var password = "" // Note: While stored as a string, this is always hashed and salted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        controller = intent.extras!!["CONTROLLER"] as IController
        dbRef = FirebaseDatabase.getInstance().reference.child("Users")

        edit_dob!!.showSoftInputOnFocus = false
        edit_dob!!.onFocusChangeListener = View.OnFocusChangeListener()
        { view: View, hasFocus: Boolean ->
            if (hasFocus) {
                val calendar = Calendar.getInstance()
                calendar.time = date ?: calendar.time
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(view.context, DatePickerDialog.OnDateSetListener()
                { _: DatePicker, newYear: Int, newMonth: Int, newDay: Int ->
                    val newCalendar = Calendar.getInstance()
                    newCalendar.set(newYear, newMonth, newDay)
                    date = newCalendar.time

                    val formattedMonth = newMonth + 1
                    val output = "$formattedMonth/$newDay/$newYear"
                    edit_dob!!.setText(output)
                }, year, month, day)
                dpd.show()
            }
        }

        btn_submit.setOnClickListener(::submitOnClick)
    }

    private fun submitOnClick(_view: View) {
        Log.d("SignupActivity","Submit button clicked")
        txt_error!!.visibility = View.GONE

        username = edit_username?.text?.toString() ?: ""
        email = edit_email?.text?.toString() ?: ""

        password = ""
        val passwordIn = edit_password?.text?.toString() ?: ""
        val passwordRetype = edit_passwordRetype?.text?.toString() ?: ""
        val verified = passwordIn == passwordRetype

        // Give error if something is not filled out correctly
        if (date == null || username.isBlank() || passwordIn.isBlank() || email.isBlank()) {
            txt_error!!.text = getString(R.string.txt_filloutError)
            txt_error!!.visibility = View.VISIBLE
        } else if (verified) {
            if (controller.verifyAge(date!!)) {
                password = passwordIn

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d("SignupActivity","Successful account creation: ${it.user!!.uid}")

                        val userId = mAuth.currentUser!!.uid

                        val childDb = dbRef.child(userId)
                        childDb.child("username").setValue(username)
                        childDb.child("email").setValue(email)
                        childDb.child("dob").setValue(date)

                        Toast.makeText(this, "Account ${email} created!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        Log.d("SignupActivity","Unsuccessful account creation")
                        Toast.makeText(this, "ERROR: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
            } else {
                txt_error!!.text = getString(R.string.txt_ageError)
                txt_error!!.visibility = View.VISIBLE
            }
        }
    }
}