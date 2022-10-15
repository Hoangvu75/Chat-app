package com.example.chatapp.AdapterClasses

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ModelClasses.Comments
import com.example.chatapp.ModelClasses.Posts
import com.example.chatapp.ModelClasses.Users
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

var imageView: ImageView? = null
var image: Uri? = null

open class PostAdapter(private val mContext: Context, private val mPost: List<Posts>) : RecyclerView.Adapter<PostAdapter.ViewHolder?>() {

    private var user: Users? = null
    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var commentAdapter: CommentAdapter? = null
    private var mComments: List<Comments>? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
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
        return ViewHolder(view as ViewGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPost[position]
        Picasso.get().load(post.getProfile()).into(holder.ivProfilePostSetting)
        holder.tvUsernamePostSetting.text = post.getUsername()
        holder.tvDateOfPost.text = post.getDate()
        holder.tvContentOfPost.text = post.getContent()
        if (post.getImage() != ""){
            Picasso.get().load(post.getImage()).into(holder.ivImageOfPost)
        } else {
            holder.ivImageOfPost.visibility = ImageView.GONE
        }

        holder.tvCommentPost.setOnClickListener {
            val dialog = Dialog(mContext)
            dialog.setContentView(R.layout.dialog_post_comment)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.show()

            recyclerView = dialog.findViewById(R.id.rvPostComments)
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.layoutManager = LinearLayoutManager(mContext)
            mComments = ArrayList()

            val refComments = FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("Comments")
            refComments.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    (mComments as ArrayList<Comments>).clear()
                    for (snapshot in dataSnapshot.children) {
                        val comment = snapshot.getValue(Comments::class.java)
                        (mComments as ArrayList<Comments>).add(comment!!)
                    }
                    commentAdapter = CommentAdapter(mContext, mComments!!)
                    recyclerView!!.adapter = commentAdapter
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })

            val ivPostCommentImage = dialog.findViewById<ImageView>(R.id.ivPostCommentImage)
            val tvPostButtonComment = dialog.findViewById<TextView>(R.id.tvPostButtonComment)
            val etWritePostComment = dialog.findViewById<EditText>(R.id.etWritePostComment)
            val tvPostImageCommentButton = dialog.findViewById<TextView>(R.id.tvPostImageCommentButton)

            imageView = ivPostCommentImage
            tvPostImageCommentButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                (mContext as Activity).startActivityForResult(intent, 4)

                tvPostButtonComment.setOnClickListener {
                    val profile: String = user?.getProfile()!!
                    val username: String = user?.getUsername()!!
                    val time: String = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                    val comment: String = etWritePostComment.text.toString()

                    val storageReference = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("post").child("post comment").child(UUID.randomUUID().toString())
                    storageReference.putFile(image!!).addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener {
                            val image: String = it.toString()
                            val commentItem = Comments(profile, username, time, comment, image)
                            FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId())
                                .child(post.getId()).child("Comments").push().setValue(commentItem)
                        }
                    }
                    imageView?.visibility = ImageView.GONE
                    etWritePostComment.text.clear()
                }
            }

            if (imageView?.visibility == ImageView.GONE){
                tvPostButtonComment.setOnClickListener {
                    val profile: String = user?.getProfile()!!
                    val username: String = user?.getUsername()!!
                    val time: String = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                    val comment: String = etWritePostComment.text.toString()
                    if (comment != "") {
                        val commentItem = Comments(profile, username, time, comment, "")
                        FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId())
                            .child(post.getId()).child("Comments").push().setValue(commentItem)
                        etWritePostComment.text.clear()
                    } else {
                        Toast.makeText(mContext, "Please write a comment", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    inner class ViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePostSetting: CircleImageView = itemView.findViewById(R.id.ivProfilePostSetting)
        val tvUsernamePostSetting: TextView = itemView.findViewById(R.id.tvUsernamePostSetting)
        val tvDateOfPost: TextView = itemView.findViewById(R.id.tvDateOfPost)
        val tvContentOfPost: TextView = itemView.findViewById(R.id.tvContentOfPost)
        val ivImageOfPost: ImageView = itemView.findViewById(R.id.ivImageOfPost)
        val tvCommentPost: TextView = itemView.findViewById(R.id.tvCommentPost)

        init {
            ivImageOfPost.setOnClickListener {
                val dialog = Dialog(mContext)
                dialog.setContentView(R.layout.dialog_popup_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
                ivDialogImage?.setImageDrawable(ivImageOfPost.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}




