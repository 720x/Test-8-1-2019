
package com.harshit.imagesearch.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.harshit.imagesearch.R
import com.harshit.imagesearch.models.UserModel


class RegisterActivity : AppCompatActivity() {
    var btnRegister: Button? = null
    private var etRegisterEmail: EditText? = null
    var etRegisterPassword: EditText? = null
    var etRegisterName: EditText? = null
    var etNameInfo: EditText? = null

    var mAuth: FirebaseAuth? = null
    var userDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etNameInfo = findViewById<EditText>(R.id.etRegisterName)
        btnRegister = findViewById<Button>(R.id.btnRegister)
        etRegisterEmail = findViewById<EditText>(R.id.etRegisterEmail)
        etRegisterPassword = findViewById<EditText>(R.id.etRegisterPassword)
        etRegisterName = findViewById<EditText>(R.id.etRegisterName)
        mAuth = FirebaseAuth.getInstance()
        btnRegister!!.setOnClickListener { view: View? ->
            createUser()
            if (etNameInfo!!.text.toString().isEmpty()) {
                etNameInfo!!.error = "Your name is required"
            } else {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ insertUserData() }, 3000)
            }
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ finish() }, 3000)

        }
    }

    open fun insertUserData() {
        userDatabase = FirebaseDatabase
            .getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        val userName = etRegisterName!!.text.toString()
        val userEmail = etRegisterEmail!!.text.toString()

        val userModels = UserModel()
        userModels.setName(userName)
        userModels.setEmail(userEmail)
        userDatabase!!.push().setValue(userModels)
    }

    private fun createUser() {
        val registerEmail = etRegisterEmail!!.text.toString()
        val registerPassword = etRegisterPassword!!.text.toString()
        if (TextUtils.isEmpty(registerEmail)) {
            etRegisterEmail!!.error = "Email cannot be empty.."
            etRegisterEmail!!.requestFocus()
        } else if (TextUtils.isEmpty(registerPassword)) {
            etRegisterPassword!!.error = "Password cannot be empty.."
            etRegisterPassword!!.requestFocus()
        } else {
            mAuth!!.createUserWithEmailAndPassword(registerEmail, registerPassword)
                .addOnCompleteListener(this,
                    OnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "User registered Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    MainActivity::class.java
                                )
                            )
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registration error: Invalid Email or Password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}