package com.example.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.AdapterClasses.UserAdapter
import com.example.chatapp.ModelClasses.Users
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.rvBoxChat)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        retrieveUsersInBoxChat()
        return view
    }

    private fun retrieveUsersInBoxChat(){
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val refBoxChat = FirebaseDatabase.getInstance().reference.child("BoxChat").child(currentUserId).child("receiver")
        refBoxChat.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    (mUsers as ArrayList<Users>).add(user!!)
                }
                userAdapter = UserAdapter(context!!, mUsers as ArrayList<Users>)
                recyclerView!!.adapter = userAdapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}