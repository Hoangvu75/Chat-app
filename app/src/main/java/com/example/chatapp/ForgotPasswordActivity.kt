package com.example.chatapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_forgot_password.*


open class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setSupportActionBar(toolbarForgotPasswordActivity)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarForgotPasswordActivity.setNavigationOnClickListener {
            finish()
            onBackPressed()
        }
    }

    fun btnConfirmOnClick(view: View){
        if(etEmailForgotPassword.text.toString().isEmpty()){
            etEmailForgotPassword.error = "Please enter your email"
        }
        else{
            showProgressDialog(this@ForgotPasswordActivity)
            FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(user in snapshot.children){
                        val userFindPassword: Users = user.getValue(Users::class.java)!!
                        if(userFindPassword.getEmail() == etEmailForgotPassword.text.toString()){
                            FirebaseAuth.getInstance().sendPasswordResetEmail(etEmailForgotPassword.text.toString()).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@ForgotPasswordActivity, "Email sent, if you do not see our mail, please check spam category", Toast.LENGTH_LONG).show()
                                    finish()
                                } else {
                                    Toast.makeText(this@ForgotPasswordActivity, "Email not sent", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}