package com.example.chatapp.AdapterClasses

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ModelClasses.Comments
import com.example.chatapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext: Context, private val mComments: List<Comments>) : RecyclerView.Adapter<CommentAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = mComments[position]
        Picasso.get().load(comment.getProfile()).into(holder.ivCommentProfile)
        holder.tvCommentUsername.text = comment.getUsername()
        holder.tvCommentTime.text = comment.getTime()
        holder.tvCommentComment.text = comment.getComment()
        if (comment.getImage().isNotEmpty()) {
            holder.ivCommentImage.visibility = View.VISIBLE
            Picasso.get().load(comment.getImage()).into(holder.ivCommentImage)
        } else {
            holder.ivCommentImage.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCommentProfile: CircleImageView = itemView.findViewById(R.id.ivCommentProfile)
        val tvCommentUsername: TextView = itemView.findViewById(R.id.tvCommentUsername)
        val tvCommentTime: TextView = itemView.findViewById(R.id.tvCommentTime)
        val tvCommentComment: TextView = itemView.findViewById(R.id.tvCommentComment)
        val ivCommentImage: ImageView = itemView.findViewById(R.id.ivCommentImage)

        init {
            ivCommentImage.setOnClickListener {
                val dialog = Dialog(mContext)
                dialog.setContentView(R.layout.dialog_popup_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.ivDialogImage)
                ivDialogImage?.setImageDrawable(ivCommentImage.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }
}
