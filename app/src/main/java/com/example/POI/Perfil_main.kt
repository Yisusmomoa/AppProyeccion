package com.example.POI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.POI.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class Perfil_main : AppCompatActivity() {

    var refUsers:DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null
    var user:Users?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_main)
        var Encript=findViewById<ImageView>(R.id.Encript)
        //Usuario logeado, mostrar info
        firebaseUser=FirebaseAuth.getInstance().currentUser
        refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        //mostrar info del usuario e imagen de usuario
        var userNameInfo=findViewById<TextView>(R.id.textViewInfoUser)
        var photoUserInfo=findViewById<ImageView>(R.id.imageViewInfoUser)

        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    user=snapshot.getValue(Users::class.java)
                    userNameInfo.setText(user!!.getUsername())
                    Picasso.get().load(user!!.getProfile()).placeholder(R.drawable.profile).into(photoUserInfo)
                    if(user!!.getEncrypt()){
                        Encript.setImageResource(R.drawable.lock)
                    }
                    else{
                        Encript.setImageResource(R.drawable.unlock)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseError","${error.message}")
            }
        })

        Encript.setOnClickListener {
            val encruserupdate=HashMap<String,Any>()
            if (user!!.getEncrypt()){
                encruserupdate["encrypt"]=false
                refUsers!!.updateChildren(encruserupdate)
            }
            else{
                encruserupdate["encrypt"]=true
                refUsers!!.updateChildren(encruserupdate)
            }

        }

        val Subgrupo = this.findViewById<ImageButton>(R.id.btn_sgrupo)
        val Chat = this.findViewById<ImageButton>(R.id.btn_chats)

        val Recompensa = this.findViewById<ImageButton>(R.id.btn_recompensa)
        val Tarea = this.findViewById<ImageButton>(R.id.btn_tarea)

/////Boton para mandar a llamar ventana de subgrupos/////

        Subgrupo.setOnClickListener{
            val VentanaSG = Intent(applicationContext,SubG_main::class.java)
            startActivity(VentanaSG)
        }
/////Boton para mandar a llamar ventana de chats/////

        Chat.setOnClickListener{
            val VentanaChat: Intent = Intent(applicationContext,Chat_main::class.java)
            startActivity(VentanaChat)
        }
/////Boton para mandar a llamar ventana de perfil/////

        Recompensa.setOnClickListener{
            val VentanaRecompensa: Intent = Intent(applicationContext,Recompensa_main::class.java)
            startActivity(VentanaRecompensa)
        }

/////Boton para mandar a llamar ventana de perfil/////

        Tarea.setOnClickListener{
            val VentanaTarea: Intent = Intent(applicationContext,Tarea_main::class.java)
            startActivity(VentanaTarea)
        }


    }
}