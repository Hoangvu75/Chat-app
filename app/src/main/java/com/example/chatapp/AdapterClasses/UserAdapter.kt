package com.example.chatapp.AdapterClasses

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.MessageActivity
import com.example.chatapp.ModelClasses.Users
import com.example.chatapp.R
import com.example.chatapp.ViewProfileActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val mContext: Context, private val mUser: List<Users>): RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    private var firebaseUser: FirebaseUser? = null

    init {
        firebaseUser = FirebaseAuth.getInstance().currentUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = mUser[position]
        holder.tvUserName.text = user.getUsername()
        Picasso.get().load(user.getProfile()).into(holder.ivProfile)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val tvUserName: TextView = itemView.findViewById(R.id.usernameItem)
        val ivProfile: CircleImageView = itemView.findViewById(R.id.profileImageItem)
        private val cvUserItem: CardView = itemView.findViewById(R.id.userItem)

        init {
            cvUserItem.setOnClickListener(this)
            cvUserItem.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {

            val receiver: Users = mUser[adapterPosition]
            val receiverId = receiver.getUID()

            val intent = Intent(itemView.context, MessageActivity::class.java)
            intent.putExtra("receiverId", receiverId.toString())
            itemView.context.startActivity(intent)
        }

        override fun onLongClick(p0: View?): Boolean {
            val dialog = Dialog(itemView.context)
            dialog.setContentView(R.layout.dialog_popup_user_item)
            dialog.setCancelable(true)

            val ivPopupCoverSearch: ImageView = dialog.findViewById(R.id.ivPopupCoverSearch)
            val ivPopupProfileSearch: CircleImageView = dialog.findViewById(R.id.ivPopupProfileSearch)
            val tvPopupBigUsernameSearch: TextView = dialog.findViewById(R.id.tvPopupBigUsernameSearch)

            Picasso.get().load(mUser[adapterPosition].getCover()).into(ivPopupCoverSearch)
            Picasso.get().load(mUser[adapterPosition].getProfile()).into(ivPopupProfileSearch)
            tvPopupBigUsernameSearch.text = mUser[adapterPosition].getUsername()

            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.show()

            ivPopupCoverSearch.setOnLongClickListener {
                val dialog = Dialog(itemView.context)
                dialog.setContentView(R.layout.dialog_popup_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
                ivDialogImage?.setImageDrawable(ivPopupCoverSearch.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage?.setOnClickListener {
                    dialog.dismiss()
                }
                return@setOnLongClickListener true
            }

            ivPopupProfileSearch.setOnLongClickListener {
                val dialog = Dialog(itemView.context)
                dialog.setContentView(R.layout.dialog_popup_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
                ivDialogImage?.setImageDrawable(ivPopupProfileSearch.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage?.setOnClickListener {
                    dialog.dismiss()
                }
                return@setOnLongClickListener true
            }

            val ivPopupFacebookIconSearch: ImageView = dialog.findViewById(R.id.ivPopupFacebookIconSearch)
            val ivPopupInstagramIconSearch: ImageView = dialog.findViewById(R.id.ivPopupInstagramIconSearch)
            val ivPopupTiktokIconSearch: ImageView = dialog.findViewById(R.id.ivPopupTiktokIconSearch)

            ivPopupFacebookIconSearch.setOnClickListener {
                val uri = Uri.parse(mUser[adapterPosition].getFacebook())
                val intent = Intent(Intent.ACTION_VIEW, uri)
                itemView.context.startActivity(intent)
            }

            ivPopupInstagramIconSearch.setOnClickListener {
                val uri = Uri.parse(mUser[adapterPosition].getInstagram())
                val intent = Intent(Intent.ACTION_VIEW, uri)
                itemView.context.startActivity(intent)
            }

            ivPopupTiktokIconSearch.setOnClickListener {
                val uri = Uri.parse(mUser[adapterPosition].getTiktok())
                val intent = Intent(Intent.ACTION_VIEW, uri)
                itemView.context.startActivity(intent)
            }

            val btnPopupSendMessageSearch: MaterialButton = dialog.findViewById(R.id.btnPopupSendMessageSearch)
            btnPopupSendMessageSearch.setOnClickListener {
                dialog.dismiss()
                val receiverId = mUser[adapterPosition].getUID()

                val intent = Intent(itemView.context, MessageActivity::class.java)
                intent.putExtra("receiverId", receiverId.toString())
                itemView.context.startActivity(intent)
            }

            val btnPopupViewProfileSearch: MaterialButton = dialog.findViewById(R.id.btnPopupViewProfileSearch)
            btnPopupViewProfileSearch.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(itemView.context, ViewProfileActivity::class.java)
                intent.putExtra("userId", mUser[adapterPosition].getUID())
                itemView.context.startActivity(intent)
            }

            return true
        }
    }
}