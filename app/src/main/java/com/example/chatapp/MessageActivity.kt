package com.example.chatapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.AdapterClasses.MessageAdapter
import com.example.chatapp.ModelClasses.Messages
import com.example.chatapp.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_message.*
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {

    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var messageAdapter: MessageAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var mMessageList: List<Messages>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        recyclerView = findViewById(R.id.rcMessageList)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        mMessageList = ArrayList()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val receiverId = intent.getStringExtra("receiverId")
        setSupportActionBar(toolbarMessageActivity)
        FirebaseDatabase.getInstance().reference.child("Users").child(receiverId!!)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    supportActionBar?.title = user?.getUsername()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarMessageActivity.setNavigationOnClickListener {
            onBackPressed()
        }

        FirebaseDatabase.getInstance().reference.child("Users").child(receiverId).child("status")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot1: DataSnapshot) {
                    FirebaseDatabase.getInstance().reference.child("Messages").child(intent.getStringExtra("receiverId")!! + firebaseUser!!.uid)
                        .child("seen").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot2: DataSnapshot) {
                                if (dataSnapshot2.value == null) {
                                    FirebaseDatabase.getInstance().reference.child("Messages").child(intent.getStringExtra("receiverId")!! + firebaseUser!!.uid)
                                        .child("seen").setValue("Not seen")
                                }
                                val status = "${dataSnapshot1.value.toString()}\n${dataSnapshot2.value.toString()}"
                                tvUserStatus.text = status
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Error", error.toString())
                            }
                        })

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })


        showMessages()

        FirebaseDatabase.getInstance().reference.child("Messages").child(firebaseUser!!.uid + intent.getStringExtra("receiverId")!!)
            .child("seen").setValue("Last seen: " + SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date()))
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                FirebaseDatabase.getInstance().reference.child("Messages").child(firebaseUser!!.uid + intent.getStringExtra("receiverId")!!)
                    .child("seen").setValue("Last seen: " + SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date()))
                handler.postDelayed(this, 100)
            }
        }
        handler.postDelayed(runnable, 100)
    }

    private fun showMessages(){
        val messagesRef = FirebaseDatabase.getInstance().reference.child("Messages")
            .child(firebaseUser!!.uid + intent.getStringExtra("receiverId")).child(firebaseUser!!.uid)
        messagesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mMessageList as ArrayList<Messages>).clear()
                for (snapshot in dataSnapshot.children){
                    val message: Messages? = snapshot.getValue(Messages::class.java)
                    (mMessageList as ArrayList<Messages>).add(message!!)
                }
                messageAdapter = MessageAdapter(this@MessageActivity, mMessageList!!, intent.getStringExtra("receiverId").toString())
                recyclerView?.adapter = messageAdapter
                recyclerView?.scrollToPosition(mMessageList!!.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.toString())
            }
        })
    }

    fun onSendMessages(view: View){
        if (etSendMessage.text.toString() != ""){
            val message = etSendMessage.text.toString()
            val messageSenderRef = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(firebaseUser!!.uid + intent.getStringExtra("receiverId")!!).child(firebaseUser!!.uid)
            val messageReceiverRef = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(intent.getStringExtra("receiverId")!! + firebaseUser!!.uid).child(intent.getStringExtra("receiverId")!!)

            val messageSenderMap = HashMap<String, Any>()
            messageSenderMap["sender"] = firebaseUser!!.uid
            messageSenderMap["message"] = message
            messageSenderMap["receiver"] = intent.getStringExtra("receiverId")!!
            messageSenderMap["time"] = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
            messageSenderMap["image"] = ""
            messageSenderMap["removed"] = "false"
            val messageId = System.currentTimeMillis().toString()
            messageSenderMap["id"] = messageId

            messageSenderRef.child(messageId).setValue(messageSenderMap)
            messageReceiverRef.child(messageId).setValue(messageSenderMap)
            etSendMessage.text.clear()

            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    FirebaseDatabase.getInstance().reference.child("BoxChat").child(intent.getStringExtra("receiverId")!!).child("receiver").child(firebaseUser!!.uid).setValue(user)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })
            FirebaseDatabase.getInstance().reference.child("Users").child(intent.getStringExtra("receiverId")!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    FirebaseDatabase.getInstance().reference.child("BoxChat").child(firebaseUser!!.uid).child("receiver").child(intent.getStringExtra("receiverId")!!).setValue(user)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })
        }
    }

    fun onSendImage(view: View){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()){
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 3)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && resultCode == RESULT_OK && data != null){
            val imageUri = data.data

            val messageSenderRef = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(firebaseUser!!.uid + intent.getStringExtra("receiverId")!!).child(firebaseUser!!.uid)
            val messageReceiverRef = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(intent.getStringExtra("receiverId")!! + firebaseUser!!.uid).child(intent.getStringExtra("receiverId")!!)

            val imageRef = FirebaseStorage.getInstance().getReference("Message Images").child(UUID.randomUUID().toString())
            imageRef.putFile(imageUri!!).addOnSuccessListener{
                imageRef.downloadUrl.addOnSuccessListener {
                    val messageSenderMap = HashMap<String, Any>()
                    messageSenderMap["sender"] = firebaseUser!!.uid
                    messageSenderMap["message"] = ""
                    messageSenderMap["receiver"] = intent.getStringExtra("receiverId")!!
                    messageSenderMap["time"] = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                    messageSenderMap["image"] = it.toString()
                    messageSenderMap["removed"] = "false"
                    val messageId = System.currentTimeMillis().toString()
                    messageSenderMap["id"] = messageId

                    messageSenderRef.child(messageId).setValue(messageSenderMap)
                    messageReceiverRef.child(messageId).setValue(messageSenderMap)

                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user: Users? = dataSnapshot.getValue(Users::class.java)
                            FirebaseDatabase.getInstance().reference.child("BoxChat").child(intent.getStringExtra("receiverId")!!).child("receiver").child(firebaseUser!!.uid).setValue(user)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Error", error.toString())
                        }
                    })
                    FirebaseDatabase.getInstance().reference.child("Users").child(intent.getStringExtra("receiverId")!!).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user: Users? = dataSnapshot.getValue(Users::class.java)
                            FirebaseDatabase.getInstance().reference.child("BoxChat").child(firebaseUser!!.uid).child("receiver").child(intent.getStringExtra("receiverId")!!).setValue(user)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Error", error.toString())
                        }
                    })
                }
            }
        }
    }
}