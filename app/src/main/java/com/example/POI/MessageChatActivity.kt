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
import com.example.POI.AdapterClasses.ChatsAdapter
import com.example.POI.Fragments.APIService
import com.example.POI.ModelClasses.Chat
import com.example.POI.ModelClasses.Users
import com.example.POI.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit:String=""
    var firebaseUser:FirebaseUser?=null
    var chatsAdapter:ChatsAdapter?=null
    var mChatList:List<Chat>?=null
    lateinit var recycler_view_chats: RecyclerView
    var reference:DatabaseReference?=null

    var notify=false
    var apiService:APIService?=null
    var user:Users?=null
    var userLogeado:Users?=null
    var refUsers:DatabaseReference?=null
    var firebaseUserlogged:FirebaseUser?=null
    var database=FirebaseDatabase.getInstance()
    private var recompensasRef=database.getReference("Recompensas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        /*val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent=Intent(this@MessageChatActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }*/

        apiService=Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)


        firebaseUserlogged=FirebaseAuth.getInstance().currentUser
        refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserlogged!!.uid)


        intent=intent
        userIdVisit=intent.getStringExtra("visit_id").toString()
        firebaseUser=FirebaseAuth.getInstance().currentUser
        recycler_view_chats=findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        var linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        recycler_view_chats.layoutManager=linearLayoutManager

        reference=FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        //esta escuchando si hay alg??n cambio en alguna rama, en este caso users
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
//esta escuchando si hay alg??n cambio en alguna rama, en este caso users
                user=p0.getValue(Users::class.java)
                username_mchat.text=user!!.getUsername()
                Picasso.get().load(user!!.getProfile()).into(profile_image_mchat)
                retrieveMessages(firebaseUser!!.uid,userIdVisit,user!!.getProfile())
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userLogeado=snapshot.getValue(Users::class.java)

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })



        send_location_btn.setOnClickListener{
            revisarPermisos()

        }

        send_message_btn.setOnClickListener {
            notify=true
            val message=findViewById<EditText>(R.id.text_message).text.toString()
            if(message==""){
                Toast.makeText(this, "Por favor, primero escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
            else{
                sendMessagetoUser(firebaseUser!!.uid,userIdVisit,message,userLogeado)
            }
            text_message.setText("")
        }

        attact_image_file_btn.setOnClickListener {
            notify=true
            val intent=Intent()
            intent.type="*/*"
            //intent.type="image/*" //original
            intent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }

        seenMessage(userIdVisit)


    }

    private fun sendMessagetoUser(senderId: String, receiverId: String, message: String, userLogeado: Users?) {
        val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key

        val messageHashMap=HashMap<String,Any?>()
        messageHashMap["sender"]=senderId
        if (userLogeado!!.getEncrypt()){
            //messageHashMap["message"]=encriptar(message)
            //  val textoCifrado = CifradoTools.cifrar(textoOriginal, "elAlumno123")
            messageHashMap["message"]=CifradoTools.cifrar(message,"elAlumno123")
            messageHashMap["encryptChat"]="True"
        }
        else{
            messageHashMap["encryptChat"]="False"
            messageHashMap["message"]=message
        }

        messageHashMap["receiver"]=receiverId
        messageHashMap["isseen"]=false
        messageHashMap["url"]=""
        messageHashMap["messageId"]=messageKey
        messageHashMap["timeStamp"]=ServerValue.TIMESTAMP

        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val chatsListReference=FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    //Se crea la recompensa
                    recompensasRef.child("EnvioMensajeIndividual")
                        .child(userLogeado.getUID().toString())
                        .setValue(userLogeado)

                    chatsListReference.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()){
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverRef=FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })


                }
            }

        //implementaci??n de push notifications usando fcm
        val usersReference=FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)
        usersReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user=p0.getValue(Users::class.java)
                if (notify){
                    sendNotification(receiverId,user!!.getUsername(),message)
                }
                notify=false
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendNotification(receiverId: String, username: String?, message: String) {
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val query=ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children){
                    val token:Token?=dataSnapshot.getValue(Token::class.java)
                    val data=Data(firebaseUser!!.uid,R.mipmap.ic_launcher, "$username: $message","New message",userIdVisit)

                    val sender=Sender(data!!,token!!.getToken().toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code()==280){
                                    if (response.body()!!.success!==1){
                                        Toast.makeText(this@MessageChatActivity,"Fail", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data!!.data!=null){
            val progressBar=ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait.....")
            progressBar.show()

            val fileUri=data.data
            val storageReference=FirebaseStorage.getInstance()
                .reference.child("Chat Images")
            val ref=FirebaseDatabase.getInstance().reference
            val messageId=ref.push().key
            //val filePath=storageReference.child("$messageId.jpg") //original
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
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()

                    val messageHashMap=HashMap<String,Any?>()
                    messageHashMap["sender"]=firebaseUser!!.uid
                    messageHashMap["message"]="sent you an image"
                    messageHashMap["receiver"]=userIdVisit
                    messageHashMap["isseen"]=false
                    messageHashMap["url"]=url
                    messageHashMap["messageId"]=messageId
                    messageHashMap["timeStamp"]=ServerValue.TIMESTAMP

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task->
                            if (task.isSuccessful){
                                progressBar.dismiss()

                                //implementaci??n de push notifications usando fcm
                                val reference=FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)
                                reference.addValueEventListener(object :ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user=p0.getValue(Users::class.java)
                                        if (notify){
                                            sendNotification(userIdVisit,user!!.getUsername(),"sent you an image")
                                        }
                                        notify=false
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })
                            }
                        }
                }
            }
        }

        if (resultCode == RESULT_OK) {

            findViewById<TextView>(R.id.text_message).text = data?.getStringExtra("ubicacion") ?: ""
        } else {

            findViewById<TextView>(R.id.text_message).text = "Error o no seleccionaste una direccion"
        }

    }

    private fun retrieveMessages(senderId: String, receiverId: String,
                                 receiverImageUrl: String) {
        mChatList=ArrayList()
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                //se cargan los mensajes del chat
                (mChatList as ArrayList<Chat>).clear()
                for(snapshot in p0.children){
                    //aqu?? se debe de dar el formato a la fecha
                    val chat=snapshot.getValue(Chat::class.java)
                    /* val dateFormater=SimpleDateFormat("dd/MM/yyyy - HH:mm:ss",Locale.getDefault())
            val fecha=dateFormater?.format(Date(DateMessage as Long))*/
                    if (chat?.getTimeStamp()!=""){
                        val dateFormater=SimpleDateFormat("dd/MM/yyyy - HH:mm:SS",Locale.getDefault())
                        val fecha=dateFormater?.format(chat?.getTimeStamp() as Long)
                        chat?.setTimeStamp(fecha!!)
                    }
                    if (chat!!.getReceiver().equals(senderId) &&
                        chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) &&
                        chat.getSender().equals(senderId)){
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter= ChatsAdapter(this@MessageChatActivity,(mChatList as ArrayList<Chat>),receiverImageUrl!!)
                    recycler_view_chats.adapter=chatsAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })



    }

    var seenListener:ValueEventListener?=null

    private fun seenMessage(userId:String){
        reference=FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener=reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children){
                    val chat=dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId)){
                        val hashMap=HashMap<String,Any>()
                        hashMap["isseen"]=true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }

    fun encriptar(mensajeAEncriptar:String ):String{
        //var cipher:Cipher= Cipher.getInstance("AES")
        var KeyGenerator:KeyGenerator= KeyGenerator.getInstance("AES")
        KeyGenerator.init(128)
        var secretKey:SecretKey=KeyGenerator.generateKey()
        var ByteArray=secretKey.encoded
        var secretKeySpec:SecretKeySpec= SecretKeySpec(ByteArray,"AES")
        var cipher:Cipher= Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec)
        var MensajeEncriptado:ByteArray=cipher.doFinal(mensajeAEncriptar.toByteArray())
        return MensajeEncriptado.toString()
        Log.d("Tag", "Cadena")

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