package com.example.chatapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.AdapterClasses.PostAdapter
import com.example.chatapp.AdapterClasses.image
import com.example.chatapp.AdapterClasses.imageView
import com.example.chatapp.ModelClasses.Posts
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_profile.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewProfileActivity : AppCompatActivity() {

    private var refThisUser: DatabaseReference? = null

    private var postAdapter: PostAdapter? = null
    private var mPosts: List<Posts>? = null
    private var recyclerView: RecyclerView? = null

    private var user: Users? = null
    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        setSupportActionBar(toolbarViewProfileActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarViewProfileActivity.setNavigationOnClickListener {
            finish()
            onBackPressed()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUsers!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(Users::class.java)
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        recyclerView = findViewById(R.id.rvPostItemViewProfile)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        mPosts = ArrayList()

        val intentThisUserId = intent.getStringExtra("userId").toString()
        refThisUser = FirebaseDatabase.getInstance().reference.child("Users").child(intentThisUserId)
        refThisUser?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Users::class.java)
                Picasso.get().load(user?.getCover()).into(ivCoverViewProfile)
                Picasso.get().load(user?.getProfile()).into(ivProfileViewProfile)
                tvBigUsernameViewProfile.text = user?.getUsername()
                supportActionBar?.title = "Profile of " + user?.getUsername()
                if (supportActionBar?.title?.length!! > 20) {
                    supportActionBar?.title = supportActionBar?.title?.substring(0, 20) + "..."
                }
                user!!.getUID()?.let { getPost(it) }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        ivCoverViewProfile.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_popup_image)
            dialog.setCancelable(true)
            val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
            ivDialogImage?.setImageDrawable(ivCoverViewProfile.drawable)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.show()
            ivDialogImage.setOnClickListener {
                dialog.dismiss()
            }
        }

        ivProfileViewProfile.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_popup_image)
            dialog.setCancelable(true)
            val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
            ivDialogImage?.setImageDrawable(ivProfileViewProfile.drawable)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.show()
            ivDialogImage.setOnClickListener {
                dialog.dismiss()
            }
        }

        if (ivImagePostViewProfile?.visibility == View.GONE){
            tvPostButtonViewProfile?.setOnClickListener {
                val postProfile: String = user?.getProfile()!!
                val postUsername: String = user?.getUsername()!!
                val postDate: String = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                val postContent: String = etWritePostViewProfile?.text.toString()
                val postImage = ""
                val postId = System.currentTimeMillis().toString()
                val postUserId = user?.getUID()!!
                if (postContent != "") {
                    val post = Posts(postProfile, postUsername, postDate, postContent, postImage, postId, postUserId)
                    FirebaseDatabase.getInstance().getReference("Posts").child(intentThisUserId).child(postId).setValue(post)
                    etWritePostViewProfile?.text?.clear()
                } else {
                    Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvPostImageButtonViewProfile?.setOnClickListener {
            onClickPostImage()
        }
    }

    private fun onClickPostImage(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 5)
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                token.continuePermissionRequest()
            }
        }).check()
    }

    private fun getPost(userId: String) {
        val refPosts = FirebaseDatabase.getInstance().getReference("Posts").child(userId)
        refPosts.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mPosts as ArrayList<Posts>).clear()
                for (snap in snapshot.children){
                    val post = snap.getValue(Posts::class.java)
                    (mPosts as ArrayList<Posts>).add(post!!)
                    Log.d("Posts", post.getUsername())
                }
                mPosts?.let { Collections.reverse(it) }
                postAdapter = PostAdapter(this@ViewProfileActivity, mPosts!!)
                recyclerView!!.adapter = postAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        postAdapter?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 4 && resultCode == RESULT_OK && data != null) {
            image = data.data!!
            imageView!!.setImageURI(image)
            imageView!!.visibility = View.VISIBLE
        } else if (requestCode == 5 && resultCode == RESULT_OK && data != null) {
            val image = data.data
            ivImagePostViewProfile?.setImageURI(image)
            ivImagePostViewProfile?.visibility = View.VISIBLE

            val intentThisUserId = intent.getStringExtra("userId").toString()
            tvPostButtonViewProfile?.setOnClickListener {
                val postProfile: String = user?.getProfile()!!
                val postUsername: String = user?.getUsername()!!
                val postDate: String = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                val postContent: String = etWritePostViewProfile?.text.toString()
                val postId: String = System.currentTimeMillis().toString()
                val postUserId: String = user?.getUID()!!

                val storageReference = FirebaseStorage.getInstance().getReference(intentThisUserId).child("post").child(UUID.randomUUID().toString())
                storageReference.putFile(image!!).addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener {
                        val postImage: String = it.toString()
                        val post = Posts(postProfile, postUsername, postDate, postContent, postImage, postId, postUserId)
                        FirebaseDatabase.getInstance().getReference("Posts").child(intentThisUserId)
                            .child(postId).setValue(post)
                    }
                }

                ivImagePostViewProfile?.visibility = View.GONE
                etWritePostViewProfile?.text?.clear()
                ivImagePostViewProfile?.setImageDrawable(null)
            }
        }
    }
}