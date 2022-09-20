package com.example.POI

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.ChatGrupalAdapter
import com.example.POI.AdapterClasses.ChatsAdapter
import com.example.POI.ModelClasses.Chat
import com.example.POI.ModelClasses.MensajeGrupal
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.activity_message_chat.attact_image_file_btn
import kotlinx.android.synthetic.main.activity_message_chat.send_message_btn
import kotlinx.android.synthetic.main.activity_message_chat.text_message
import kotlinx.android.synthetic.main.activity_message_chat_group.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageChatActivityGroup : AppCompatActivity() {

    var grupoId:String=""
    var firebaseUser: FirebaseUser?=null
   // private val listaMensajes = mutableListOf<MensajeGrupal>()
    var mChatList:List<MensajeGrupal>?=null
    lateinit var recycler_view_chats: RecyclerView
    var reference: DatabaseReference?=null
    var chatsAdapter: ChatGrupalAdapter?=null

    //referencia a la base de datos
    var database=FirebaseDatabase.getInstance()
    //crear una ramita en la bd que se llamará groups
    val groupRef=database.getReference("ChatGrupal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat_group)

        intent=intent
        grupoId=intent.getStringExtra("visit_id").toString()
        firebaseUser= FirebaseAuth.getInstance().currentUser
        recycler_view_chats=findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        var linearLayoutManager= LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        recycler_view_chats.layoutManager=linearLayoutManager
        firebaseUser=FirebaseAuth.getInstance().currentUser
        recycler_view_chats.adapter = chatsAdapter
        send_message_btn.setOnClickListener {
            val message=findViewById<EditText>(R.id.text_message).text.toString()
            if(message==""){
                Toast.makeText(this, "Por favor, primero escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
            else{
                enviarMensaje(MensajeGrupal("", message, firebaseUser!!.uid, ServerValue.TIMESTAMP,grupoId))
            }
            text_message.setText("")
        }

        attact_image_file_btn.setOnClickListener {
            val intent= Intent()
            //intent.type="image/*"
            intent.type="*/*"
            intent.action= Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Pick File"), 438)
        }

        recibirMensajes()

        send_location_btn2.setOnClickListener{
            revisarPermisos()
        }



    }








    private fun recibirMensajes() {
        mChatList=ArrayList()
        (mChatList as ArrayList<MensajeGrupal>).clear()

        val chatgrupal=groupRef.child(grupoId).child("mensajes")
        chatgrupal.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mChatList as ArrayList<MensajeGrupal>).clear()
                for (snap in snapshot.children) {
                    var mensaje: MensajeGrupal = snap.getValue(MensajeGrupal::class.java) as MensajeGrupal //se mapea de un texto a un objeto, en este caso mensaje
                    if(mensaje.timeStamp !=""){
                        val dateFormater= SimpleDateFormat("dd/MM/yyyy - HH:mm:SS", Locale.getDefault())
                        val fecha=dateFormater?.format(mensaje?.timeStamp as Long)
                        mensaje.timeStamp=fecha!!
                    }
                    if (mensaje.de == firebaseUser!!.uid.toString())
                        mensaje.esMio = true
                        (mChatList as ArrayList<MensajeGrupal>).add(mensaje)

                }
                if ((mChatList as ArrayList<MensajeGrupal>).size>0) {
                    chatsAdapter?.notifyDataSetChanged()

                    recycler_view_chats.smoothScrollToPosition((mChatList as ArrayList<MensajeGrupal>).size - 1)
                    //moviendo el recycler view conforme se actualiza el contenido
                }
                chatsAdapter= ChatGrupalAdapter(this@MessageChatActivityGroup,(mChatList as ArrayList<MensajeGrupal>))
                recycler_view_chats.adapter=chatsAdapter
                //recycler_view_chats.adapter=ChatGrupalAdapter(this@SubG_main,listaGrupos)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun enviarMensaje(mensaje: MensajeGrupal) {
        //val groupRef=database.getReference("ChatGrupal")

        val mensajeFirebase = groupRef.child(grupoId)
            .child("mensajes").push()
        mensaje.id = mensajeFirebase.key ?: ""
        mensajeFirebase.setValue(mensaje)
    }

    /*
    * private fun recibirMensajes() {
        mChatList=ArrayList()
        val chatgrupal=groupRef.child(grupoId)
        chatgrupal.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mChatList as ArrayList<MensajeGrupal>).clear()
                for (snap in snapshot.children) {
                    val mensaje: MensajeGrupal = snap.getValue(MensajeGrupal::class.java) as MensajeGrupal //se mapea de un texto a un objeto, en este caso mensaje
                    if (mensaje.de == firebaseUser!!.uid.toString())
                        mensaje.esMio = true
                    (mChatList as ArrayList<MensajeGrupal>).add(mensaje)
                }
                if ((mChatList as ArrayList<MensajeGrupal>).size>0) {
                    chatsAdapter?.notifyDataSetChanged()
                    chatsAdapter= ChatGrupalAdapter(this@MessageChatActivityGroup,(mChatList as ArrayList<Chat>))
                    recycler_view_chats.smoothScrollToPosition((mChatList as ArrayList<MensajeGrupal>).size - 1) //moviendo el recycler view conforme se actualiza el contenido
                }
                //recycler_view_chats.adapter=ChatGrupalAdapter(this@SubG_main,listaGrupos)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    *
    * */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && data!=null && data.data!=null){
            val progressBar= ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait.....")
            progressBar.show()
            Log.d("TypeFile","data: "+data)
            Log.d("TypeFile","data.data: "+data.data)
            Log.d("TypeFile","data.dataString: "+data.dataString)
            Log.d("TypeFile","data.flags: "+data.flags)

            val fileUri=data.data
            Log.d("TypeFile","fileUri.: "+fileUri)

            val storageReference= FirebaseStorage.getInstance()
                .reference.child("Chat Images")
            //val ref=FirebaseDatabase.getInstance().reference
            //val messageId=ref.push().key
            val mensajeFirebase = groupRef.child(grupoId)
                .child("mensajes").push()
            val messageId=mensajeFirebase.key?:""
            val filePath=storageReference.child("$messageId")
            var uploadTask: StorageTask<*>
            uploadTask=filePath.putFile(fileUri!!)

            uploadTask.continueWithTask<Uri?>(Continuation<UploadTask
            .TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener {
                task->
                if (task.isSuccessful){
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()
                    var mensaje=MensajeGrupal("", "Imagen",
                        firebaseUser!!.uid,
                    ServerValue.TIMESTAMP,grupoId,url)
                    mensaje.id=messageId
                    mensajeFirebase.setValue(mensaje)
                    progressBar.dismiss()
                }
                /*
                *   val mensajeFirebase = groupRef.child(grupoId)
            .child("mensajes").push()
        mensaje.id = mensajeFirebase.key ?: ""
        mensajeFirebase.setValue(mensaje)
                *
                *
                * */

            }
        }

        if (resultCode == RESULT_OK) {

            findViewById<TextView>(R.id.text_message).text = data?.getStringExtra("ubicacion") ?: ""
        } else {

            findViewById<TextView>(R.id.text_message).text = "Error o no seleccionaste una direccion"
        }
    }



    private fun abrirMapa() {

        startActivityForResult(Intent(this, MapsActivity::class.java), 1)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Se requiere aceptar el permiso", Toast.LENGTH_SHORT).show()
                revisarPermisos()
            } else {
                Toast.makeText(this, "Permisio concedido", Toast.LENGTH_SHORT).show()
                abrirMapa()
            }
        }
    }

    private fun revisarPermisos() {
        // Apartir de Android 6.0+ necesitamos pedir el permiso de ubicacion
        // directamente en tiempo de ejecucion de la app
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tenemos permiso para la ubicacion
            // Solicitamos permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        } else {
            // Ya se han concedido los permisos anteriormente
            abrirMapa()
        }
    }




}