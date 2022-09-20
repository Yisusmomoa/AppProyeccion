package com.example.POI.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.UserAdapter
import com.example.POI.ModelClasses.Chatlist
import com.example.POI.ModelClasses.Users
import com.example.POI.Notifications.Token
import com.example.POI.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_perfil_main.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var userAdapter: UserAdapter?=null
    private var mUsers:List<Users>?=null
    private var usersChatList:List<Chatlist>?=null

    lateinit var recycler_view_chatlist:RecyclerView
    private var firebaseUser:FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_chats,
            container, false)

        recycler_view_chatlist=view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager=LinearLayoutManager(context)

        firebaseUser=FirebaseAuth.getInstance().currentUser

        usersChatList=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for (dataSnapshot in p0.children){
                    val chatlist=dataSnapshot.getValue(Chatlist::class.java)
                    (usersChatList as ArrayList).add(chatlist!!)

                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        updateToken(FirebaseMessaging.getInstance().token)

        return view
    }

    private fun updateToken(token: Any) {
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1=Token(token.toString()!!)
        ref.child(firebaseUser!!.uid).setValue(token1)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun retrieveChatList(){
        mUsers=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children){
                    val user=dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!){
                        if(user!!.getUID().equals(eachChatList.getId())){
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter= context?.let { UserAdapter(it,(mUsers as ArrayList<Users>),true) }
                recycler_view_chatlist.adapter=userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}