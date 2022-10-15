package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chatapp.AdapterClasses.PostAdapter
import com.example.chatapp.AdapterClasses.image
import com.example.chatapp.AdapterClasses.imageView
import com.example.chatapp.Fragments.ChatFragment
import com.example.chatapp.Fragments.SearchFragment
import com.example.chatapp.Fragments.SettingsFragment
import com.example.chatapp.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var postAdapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        setSupportActionBar(toolbarMain)
        supportActionBar?.title = ""

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatFragment(), "Chat")
        viewPagerAdapter.addFragment(SearchFragment(), "Search")
        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)
                    userName.text = user!!.getUsername()
                    Picasso.get().load(user.getProfile()).into(profileImage)
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        refUsers!!.child("status").onDisconnect().setValue("offline")
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles: ArrayList<String> = ArrayList<String>()

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }
    }

    override fun onStart() {
        super.onStart()
        refUsers!!.child("status").setValue("online")
    }

    fun onLogout(view: View) {
        // show logout dialog
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            refUsers!!.child("status").setValue("offline")
            if (FirebaseAuth.getInstance().currentUser == null) {
                this.finishAndRemoveTask()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        postAdapter?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 4 && resultCode == RESULT_OK && data != null) {
            image = data.data!!
            imageView!!.setImageURI(image)
            imageView!!.visibility = View.VISIBLE
        }
    }
}