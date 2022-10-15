package com.example.chatapp.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.AdapterClasses.PostAdapter
import com.example.chatapp.ModelClasses.Posts
import com.example.chatapp.ModelClasses.Users
import com.example.chatapp.R
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private var user: Users? = null

    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var ivCoverSetting: ImageView? = null
    private var ivProfileSetting: CircleImageView? = null

    private var tvEditInfo: TextView? = null

    private var tvBigUsernameSetting: TextView? = null
    private var tvUsernameSetting: TextView? = null
    private var tvEmailSetting: TextView? = null

    private var tvFacebookSetting: TextView? = null
    private var tvInstagramSetting: TextView? = null
    private var tvTiktokSetting: TextView? = null

    private var etWritePost: EditText? = null
    private var tvPostImageButton: TextView? = null
    private var ivImagePost: ImageView? = null
    private var tvPostButton: TextView? = null

    private var postAdapter: PostAdapter? = null
    private var mPosts: List<Posts>? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUsers!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(Users::class.java)
                    tvBigUsernameSetting?.text = user!!.getUsername()
                    tvUsernameSetting?.text = user!!.getUsername()
                    if (user!!.getUsername()?.length!! > 25){
                        tvUsernameSetting?.text = user!!.getUsername()?.substring(0,25) + "..."
                    }
                    tvEmailSetting?.text = user!!.getEmail()
                    if (user!!.getEmail()?.length!! > 18){
                        tvEmailSetting?.text = user!!.getEmail()?.substring(0,18) + "..."
                    }
                    tvFacebookSetting?.text = user!!.getFacebook()
                    if (user!!.getFacebook()?.length!! > 25){
                        tvFacebookSetting?.text = user!!.getFacebook()?.substring(0, 25) + "..."
                    }
                    tvInstagramSetting?.text = user!!.getInstagram()
                    if (user!!.getInstagram()?.length!! > 25){
                        tvInstagramSetting?.text = user!!.getInstagram()?.substring(0, 25) + "..."
                    }
                    tvTiktokSetting?.text = user!!.getTiktok()
                    if (user!!.getTiktok()?.length!! > 25){
                        tvTiktokSetting?.text = user!!.getTiktok()?.substring(0, 25) + "..."
                    }
                    Picasso.get().load(user!!.getProfile()).into(ivProfileSetting)
                    Picasso.get().load(user!!.getCover()).into(ivCoverSetting)
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        ivCoverSetting = view.findViewById(R.id.ivCoverSetting)
        ivCoverSetting?.setOnClickListener {
            setCoverImage()
        }
        ivCoverSetting?.setOnLongClickListener {
            val dialog = context?.let { it -> Dialog(it) }
            dialog?.setContentView(R.layout.dialog_popup_image)
            dialog?.setCancelable(true)
            val ivDialogImage = dialog?.findViewById<ImageView>(R.id.ivDialogImage)
            ivDialogImage?.setImageDrawable(ivCoverSetting?.drawable)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.show()
            ivDialogImage?.setOnClickListener {
                dialog.dismiss()
            }
            true
        }

        ivProfileSetting = view.findViewById(R.id.ivProfileSetting)
        ivProfileSetting?.setOnClickListener {
            setProfileImage()
        }
        ivProfileSetting?.setOnLongClickListener {
            val dialog = context?.let { it -> Dialog(it) }
            dialog?.setContentView(R.layout.dialog_popup_image)
            dialog?.setCancelable(true)
            val ivDialogImage = dialog?.findViewById<ImageView>(R.id.ivDialogImage)
            ivDialogImage?.setImageDrawable(ivProfileSetting?.drawable)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.show()
            ivDialogImage?.setOnClickListener {
                dialog.dismiss()
            }
            true
        }

        tvEditInfo = view.findViewById(R.id.tvEditInfo)
        tvEditInfo?.setOnClickListener {
            val dialog = context?.let { it -> Dialog(it) }
            dialog?.setContentView(R.layout.dialog_popup_edit_info)
            dialog?.setCancelable(true)
            val etUsernameEdit = dialog?.findViewById<TextView>(R.id.etUsernameEdit)
            etUsernameEdit?.text = user?.getUsername()
            val etEmailEdit = dialog?.findViewById<TextView>(R.id.etEmailEdit)
            etEmailEdit?.text = user?.getEmail()
            val etFacebookEdit = dialog?.findViewById<TextView>(R.id.etFacebookEdit)
            etFacebookEdit?.text = user?.getFacebook()
            val etInstagramEdit = dialog?.findViewById<TextView>(R.id.etInstagramEdit)
            etInstagramEdit?.text = user?.getInstagram()
            val etTiktokEdit = dialog?.findViewById<TextView>(R.id.etTiktokEdit)
            etTiktokEdit?.text = user?.getTiktok()
            dialog?.window?.setGravity(Gravity.CENTER)
            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog?.show()
            val btnSaveEdit = dialog?.findViewById<Button>(R.id.btnSaveEdit)
            btnSaveEdit?.setOnClickListener {
                val username = etUsernameEdit?.text.toString()
                val email = etEmailEdit?.text.toString()
                val facebook = etFacebookEdit?.text.toString()
                val instagram = etInstagramEdit?.text.toString()
                val tiktok = etTiktokEdit?.text.toString()
                if (username.isEmpty() || email.isEmpty() || facebook.isEmpty() || instagram.isEmpty() || tiktok.isEmpty()){
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                } else {
                    refUsers!!.child("username").setValue(username)
                    refUsers!!.child("email").setValue(email)
                    refUsers!!.child("facebook").setValue(facebook)
                    refUsers!!.child("instagram").setValue(instagram)
                    refUsers!!.child("tiktok").setValue(tiktok)
                    refUsers!!.child("search").setValue(username.toLowerCase())
                    dialog.dismiss()
                }
            }
        }

        tvBigUsernameSetting = view.findViewById(R.id.tvBigUsernameSetting)
        tvUsernameSetting = view.findViewById(R.id.tvUsernameSetting)
        tvEmailSetting = view.findViewById(R.id.tvEmailSetting)

        tvFacebookSetting = view.findViewById(R.id.tvFacebookSetting)
        tvFacebookSetting?.setOnClickListener {
            val uri = Uri.parse(user?.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        tvInstagramSetting = view.findViewById(R.id.tvInstagramSetting)
        tvInstagramSetting?.setOnClickListener {
            val uri = Uri.parse(user?.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        tvTiktokSetting = view.findViewById(R.id.tvTiktokSetting)
        tvTiktokSetting?.setOnClickListener {
            val uri = Uri.parse(user?.getTiktok())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        tvPostButton = view.findViewById(R.id.tvPostButton)
        etWritePost = view.findViewById(R.id.etWritePost)
        ivImagePost = view.findViewById(R.id.ivImagePost)
        tvPostImageButton = view.findViewById(R.id.tvPostImageButton)
        tvPostImageButton?.setOnClickListener {
            onClickPostImage(view)
        }

        if (ivImagePost?.visibility == View.GONE){
            tvPostButton?.setOnClickListener {
                val postProfile: String = user?.getProfile()!!
                val postUsername: String = user?.getUsername()!!
                val postDate: String = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                val postContent: String = etWritePost?.text.toString()
                val postImage = ""
                val postId = System.currentTimeMillis().toString()
                val postUserId = user?.getUID()!!
                if (postContent != "") {
                    val post = Posts(postProfile, postUsername, postDate, postContent, postImage, postId, postUserId)
                    FirebaseDatabase.getInstance().getReference("Posts").child(firebaseUser!!.uid).child(postId).setValue(post)
                    etWritePost?.text?.clear()
                } else {
                    Toast.makeText(context, "Please write something", Toast.LENGTH_SHORT).show()
                }
            }
        }

        recyclerView = view.findViewById(R.id.rvPostItem)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mPosts = ArrayList()
        getPost()

        return view
    }

    private fun getPost(){
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refPosts = FirebaseDatabase.getInstance().getReference("Posts").child(firebaseUserID)
        refPosts.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mPosts as ArrayList<Posts>).clear()
                for (snap in snapshot.children){
                    val post = snap.getValue(Posts::class.java)
                    (mPosts as ArrayList<Posts>).add(post!!)
                    Log.d("Posts", post.getUsername())
                }
                mPosts?.let { Collections.reverse(it) }
                postAdapter = PostAdapter(context!!, mPosts!!)
                recyclerView!!.adapter = postAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun onClickPostImage(view: View){
        Dexter.withContext(context).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 2)
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                token.continuePermissionRequest()
            }
        }).check()
    }

    private fun setCoverImage(){
        Dexter.withContext(context).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 0)
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    TODO("Not yet implemented")
                }
        }).check()
    }

    private fun setProfileImage(){
        Dexter.withContext(context).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 1)
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    TODO("Not yet implemented")
                }
        }).check()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val image = data.data
            val storageReference = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("cover")
            storageReference.putFile(image!!).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    refUsers!!.child("cover").setValue(it.toString())
                }
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val image = data.data
            val storageReference = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("profile")
            storageReference.putFile(image!!).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    refUsers!!.child("profile").setValue(it.toString())
                }
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            val image = data.data
            ivImagePost?.setImageURI(image)
            ivImagePost?.visibility = View.VISIBLE

            tvPostButton?.setOnClickListener {
                val postProfile: String = user?.getProfile()!!
                val postUsername: String = user?.getUsername()!!
                val postDate: String = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                val postContent: String = etWritePost?.text.toString()
                val postId: String = System.currentTimeMillis().toString()
                val postUserId: String = user?.getUID()!!

                val storageReference = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("post").child(UUID.randomUUID().toString())
                storageReference.putFile(image!!).addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener {
                        val postImage: String = it.toString()
                        val post = Posts(postProfile, postUsername, postDate, postContent, postImage, postId, postUserId)
                        FirebaseDatabase.getInstance().getReference("Posts").child(firebaseUser!!.uid)
                            .child(postId).setValue(post)
                    }
                }

                ivImagePost?.visibility = View.GONE
                etWritePost?.text?.clear()
                ivImagePost?.setImageDrawable(null)
            }
        }
    }
}