package com.example.POI.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.Home
import com.example.POI.MessageChatActivity
import com.example.POI.ModelClasses.Chat
import com.example.POI.ModelClasses.Users
import com.example.POI.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text



class UserAdapter(mContext:Context, mUsers:List<Users>, 
                  isChatCheck:Boolean):RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    private val mContext:Context
    private val mUsers:List<Users>
    private var isChatCheck:Boolean
    var lastMsg:String=""

    init {
        this.mUsers=mUsers
        this.mContext=mContext
        this.isChatCheck=isChatCheck

    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view:View=LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup,false)
        return UserAdapter.ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user:Users=mUsers[i]
        holder.userNammeTxt.text=user.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)

        if (isChatCheck){
            retrieveLastMessage(user.getUID(),holder.lastMessageTxt)
        }
        else{
            holder.lastMessageTxt.visibility=View.GONE
        }

        if(isChatCheck){
            if (user.getStatus()=="online"){
                holder.onlineImageView.visibility=View.VISIBLE
                holder.offlineImageView.visibility=View.GONE
            }
            else{
                holder.onlineImageView.visibility=View.GONE
                holder.offlineImageView.visibility=View.VISIBLE
            }
        }
        else{
            holder.onlineImageView.visibility=View.GONE
            holder.offlineImageView.visibility=View.GONE
        }

        holder.itemView.setOnClickListener {
            val options= arrayOf<CharSequence>(
                "Send message",
                "Visit profile"
            )
            val builder:AlertDialog.Builder=AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, position ->
                if (position==0){
                    val intent= Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mContext.startActivity(intent)
                }
                if (position==1){

                }
            })
            builder.show()

        }
/*
* 2022-04-18 21:19:22.011 9862-9862/com.example.POI E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.example.POI, PID: 9862
    java.lang.NullPointerException
        at com.example.POI.Fragments.SearchFragment$retrieveAllUsers$1.onDataChange(SearchFragment.kt:103)
        at com.google.firebase.database.core.ValueEventRegistration.fireEvent(ValueEventRegistration.java:75)
        at com.google.firebase.database.core.view.DataEvent.fire(DataEvent.java:63)
        at com.google.firebase.database.core.view.EventRaiser$1.run(EventRaiser.java:55)
        at android.os.Handler.handleCallback(Handler.java:938)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at android.os.Looper.loop(Looper.java:223)
        at android.app.ActivityThread.main(ActivityThread.java:7656)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:592)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:947)
*
* */

    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {
        lastMsg="Default msg"
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children){
                    val chat:Chat?=dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser!=null && chat!=null){
                        if (chat.getReceiver()==firebaseUser!!.uid
                            && chat.getSender()==chatUserId ||
                                chat.getReceiver()==chatUserId &&
                                chat.getSender()==firebaseUser!!.uid) {
                            lastMsg=chat.getMessage()!!
                        }
                    }
                }
                when(lastMsg){
                    "defaultMsg"->lastMessageTxt.text="No Message"
                    "sent you an image." ->lastMessageTxt.text="image sent"
                    else ->lastMessageTxt.text=lastMsg
                }
                lastMsg="defaultMsg"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var userNammeTxt:TextView
        var profileImageView:CircleImageView
        var onlineImageView:CircleImageView
        var offlineImageView:CircleImageView
        var lastMessageTxt:TextView

        init {
            userNammeTxt=itemView.findViewById(R.id.username)
            profileImageView=itemView.findViewById(R.id.profileImg)
            onlineImageView=itemView.findViewById(R.id.imageOnline)
            offlineImageView=itemView.findViewById(R.id.imageOffline)
            lastMessageTxt=itemView.findViewById(R.id.messageLast)

        }
    }



}