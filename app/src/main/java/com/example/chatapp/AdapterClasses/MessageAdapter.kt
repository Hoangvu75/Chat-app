package com.example.chatapp.AdapterClasses

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ModelClasses.Messages
import com.example.chatapp.ModelClasses.Users
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(private val mContext: Context, private val mMessageList: List<Messages>, private val mReceiverId: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.sender_chat_layout, parent, false)
                SenderViewHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.receiver_chat_layout, parent, false)
                ReceiverViewHolder(view)
            }
            else -> {
                null!!
            }
        }
    }

    inner class SenderViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val tvSenderMessage: TextView = itemView.findViewById(R.id.tvSenderMessage)
        val tvTimeOfMessage: TextView = itemView.findViewById(R.id.tvTimeOfMessage)
        val ivSenderImage: ImageView = itemView.findViewById(R.id.ivSenderImage)
        val layoutForMessage: LinearLayout = itemView.findViewById(R.id.layoutForMessage)
        init {
            layoutForMessage.setOnClickListener {
                if (tvTimeOfMessage.visibility == View.GONE) {
                    tvTimeOfMessage.visibility = View.VISIBLE
                } else if (tvTimeOfMessage.visibility == View.VISIBLE) {
                    tvTimeOfMessage.visibility = View.GONE
                }
            }

            ivSenderImage.setOnClickListener {
                val dialog = Dialog(mContext)
                dialog.setContentView(R.layout.dialog_popup_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
                ivDialogImage?.setImageDrawable(ivSenderImage.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    inner class ReceiverViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val tvReceiverMessage: TextView = itemView.findViewById(R.id.tvReceiverMessage)
        val tvTimeOfMessage: TextView = itemView.findViewById(R.id.tvTimeOfMessage)
        val ivReceiverImage: ImageView = itemView.findViewById(R.id.ivReceiverImage)
        val ivReceiverProfileImage: CircleImageView = itemView.findViewById(R.id.ivReceiverProfileImage)
        private val layoutForMessage: LinearLayout = itemView.findViewById(R.id.layoutForMessage)
        init {
            layoutForMessage.setOnClickListener {
                if (tvTimeOfMessage.visibility == View.GONE) {
                    tvTimeOfMessage.visibility = View.VISIBLE
                } else if (tvTimeOfMessage.visibility == View.VISIBLE) {
                    tvTimeOfMessage.visibility = View.GONE
                }
            }

            ivReceiverImage.setOnClickListener {
                val dialog = Dialog(mContext)
                dialog.setContentView(R.layout.dialog_popup_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
                ivDialogImage?.setImageDrawable(ivReceiverImage.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SenderViewHolder -> {
                holder.tvSenderMessage.text = mMessageList[position].getMessage()
                holder.tvTimeOfMessage.text = mMessageList[position].getTime()
                if (mMessageList[position].getImage() != "" && mMessageList[position].getRemoved() == "false"){
                    Picasso.get().load(mMessageList[position].getImage()).into(holder.ivSenderImage)
                    holder.tvSenderMessage.visibility = View.GONE
                    holder.ivSenderImage.visibility = View.VISIBLE
                }
                if (mMessageList[position].getRemoved() == "true"){
                    holder.tvSenderMessage.text = "This message was removed"
                    holder.tvSenderMessage.setTypeface(holder.tvSenderMessage.typeface, Typeface.ITALIC)
                }
                holder.layoutForMessage.setOnLongClickListener {
                    deleteMessage(position)
                    false
                }
                holder.ivSenderImage.setOnLongClickListener {
                    deleteMessage(position)
                    false
                }
            }
            is ReceiverViewHolder -> {
                holder.tvReceiverMessage.text = mMessageList[position].getMessage()
                holder.tvTimeOfMessage.text = mMessageList[position].getTime()
                FirebaseDatabase.getInstance().reference.child("Users").child(mReceiverId).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.getValue(Users::class.java)
                            if (user?.getProfile() != "default") {
                                Picasso.get().load(user?.getProfile()).into(holder.ivReceiverProfileImage)
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
                if (holder.tvReceiverMessage.text == ""){
                    Picasso.get().load(mMessageList[position].getImage()).into(holder.ivReceiverImage)
                    holder.tvReceiverMessage.visibility = View.GONE
                    holder.ivReceiverImage.visibility = View.VISIBLE
                }
                if (mMessageList[position].getRemoved() == "true"){
                    holder.tvReceiverMessage.text = "This message was removed"
                    holder.tvReceiverMessage.setTypeface(holder.tvReceiverMessage.typeface, Typeface.ITALIC)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = firebaseUser!!.uid
        return if (mMessageList[position].getSender() == currentUserId) {
            1
        } else {
            2
        }
    }

    private fun deleteMessage(position: Int) {
        if (mMessageList[position].getRemoved() == "false") {
            val dialog = AlertDialog.Builder(mContext)
            dialog.setTitle("Delete Message")
            dialog.setMessage("Are you sure you want to delete this message?")
            dialog.setPositiveButton("Yes") { _, _ ->
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                FirebaseDatabase.getInstance().getReference("Messages").child(firebaseUser!!.uid + mReceiverId)
                    .child(firebaseUser.uid).child(mMessageList[position].getId().toString()).child("removed").setValue("true")
                FirebaseDatabase.getInstance().getReference("Messages").child(mReceiverId + firebaseUser.uid)
                    .child(mReceiverId).child(mMessageList[position].getId().toString()).child("removed").setValue("true")
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
        }
    }
}