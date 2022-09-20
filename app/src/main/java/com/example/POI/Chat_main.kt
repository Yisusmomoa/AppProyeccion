package com.example.POI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.POI.Fragments.ChatsFragment
import com.example.POI.Fragments.SearchFragment
import com.example.POI.Fragments.SettingsFragment
import com.example.POI.ModelClasses.Chat
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Chat_main : AppCompatActivity() {
    var firebaseUser: FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        val mandarmsj1a1=findViewById<Button>(R.id.btnChat1a1)

        val tabLayout:TabLayout=findViewById(R.id.tab_layout)
        val viewPager:ViewPager=findViewById<ViewPager>(R.id.view_pager)


        /*val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatsFragment(),"chats")
        viewPagerAdapter.addFragment(SearchFragment(),"search")
        viewPagerAdapter.addFragment(SettingsFragment(),"settings")
        viewPager.adapter=viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)*/
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val ref= FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages=0
                for(dataSnapshot in p0.children){
                    val chat =dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.getIsseen()){
                        countUnreadMessages+=1
                    }
                }
                if (countUnreadMessages==0){
                    viewPagerAdapter.addFragment(ChatsFragment(),"chats")
                }
                else{
                    viewPagerAdapter.addFragment(ChatsFragment(),"($countUnreadMessages) chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(),"search")
                viewPagerAdapter.addFragment(SettingsFragment(),"settings")
                viewPager.adapter=viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        mandarmsj1a1.setOnClickListener {
            startActivity(Intent(this, Activity_Main_Email::class.java))

        }


    }

    internal class ViewPagerAdapter(fragmentManager:FragmentManager):
        FragmentPagerAdapter(fragmentManager){

        private val fragments:ArrayList<Fragment>
        private val titles:ArrayList<String>
        init {
            fragments= ArrayList<Fragment>()
            titles=ArrayList<String>()

        }
        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment,title:String){
            fragments.add(fragment)
            titles.add(title)

        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }

    }

    private fun updateStatus(status:String){
        val ref=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap=HashMap<String, Any>()
        hashMap["status"]=status;
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }

}