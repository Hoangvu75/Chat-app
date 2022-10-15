package com.example.chatapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbarLoginActivity)
        supportActionBar?.title = "Chatting Application"

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    fun onLoginViaFacebook(view: View) {
        Toast.makeText(this, "Developer chưa đủ trình để phát triển tính năng này", Toast.LENGTH_LONG).show()
    }

    fun onLoginViaGoogle(view: View) {
        Toast.makeText(this, "Developer chưa đủ trình để phát triển tính năng này", Toast.LENGTH_LONG).show()
    }

    private fun loginUser() {
        val email = etEmailLogin.text.toString()
        val password = etPasswordLogin.text.toString()
        if (email == "" || password == "") {
            Toast.makeText(this@LoginActivity, "Please enter email and password", Toast.LENGTH_SHORT).show()
        } else {
            showProgressDialog(this@LoginActivity)
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUser = mAuth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        Log.d("LoginActivity", "onStart: ${firebaseUser?.email}")
        if (firebaseUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (firebaseUser == null) {
            //
        }
    }
}

fun showProgressDialog(context: Context) {
    val progressDialog = Dialog(context)
    progressDialog.setContentView(R.layout.dialog_custom_progress)
    progressDialog.show()
}
