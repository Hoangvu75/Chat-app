package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUser: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbarRegisterActivity)
        supportActionBar?.title = "Register new account"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarRegisterActivity.setNavigationOnClickListener {
            finish()
            onBackPressed()
        }

        mAuth = FirebaseAuth.getInstance()

        btnConfirmRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username = etUsernameRegister.text.toString()
        val email = etEmailRegister.text.toString()
        val password = etPasswordRegister.text.toString()
        val confirmPassword = etConfirmPasswordRegister.text.toString()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Please fill up the fields", Toast.LENGTH_SHORT).show()
        } else if (password != confirmPassword) {
            Toast.makeText(this@RegisterActivity, "Please confirm correct password", Toast.LENGTH_SHORT).show()
        } else {
            showProgressDialog(this@RegisterActivity)
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chatting-application-64e02.appspot.com/o/userprofile.jpg?alt=media&token=7029051e-4e2a-429d-b5ae-40814104203f"
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chatting-application-64e02.appspot.com/o/cover.jpg?alt=media&token=7fc5fa28-14bb-41d1-9eaf-505fdd96d5e2"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["tiktok"] = "https://m.tiktok.com"
                        userHashMap["email"] = email
                        userHashMap["password"] = password

                        Toast.makeText(this@RegisterActivity, "Successfully registered", Toast.LENGTH_LONG).show()

                        refUser.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    finish()
                                } else {
                                    Toast.makeText(this@RegisterActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}