package com.example.POI

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.ComentariosAdapter
import com.example.POI.AdapterClasses.ComentariosAdapter2
import com.example.POI.AdapterClasses.PublicacionAdapter
import com.example.POI.ModelClasses.Comentario
import com.example.POI.ModelClasses.Grupo
import com.example.POI.ModelClasses.Publicacion
import com.example.POI.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.protobuf.Value
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_publicacion.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class Publicacion : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance() //primero obtener la referencia a la base de datos
    //esta representando nuestra base de datos, con esto podemos hacer un insert, update, etc
    private var groupRef = database.getReference("groups")

    private var firebaseUser= FirebaseAuth.getInstance().currentUser
    private var recompensasRef=database.getReference("Recompensas")

    private var grupo: Grupo?=null
    private var publicacion:Publicacion?=null
    var idPublicacion:String?=""
    var usuarioComenta:Users?=null
    lateinit var rvComentarios:RecyclerView
    private lateinit var comentariosList:ArrayList<Comentario>
    //private var comentariosAdapter2=ComentariosAdapter2()
    var ComentarioPublicacion:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicacion)
        val TxtUsernamePublicacion=findViewById<TextView>(R.id.TxtUsernamePublicacion)
        val ProfileImgPublicacion=findViewById<ImageView>(R.id.ProfileImgPublicacion)
        val CuerpoPublicacion=findViewById<TextView>(R.id.CuerpoPublicacion)
        val ImgPublicacion=findViewById<ImageView>(R.id.ImgPublicacion)
        val FechaPublicacion=findViewById<TextView>(R.id.FechaPublicacion)
        val FlechitaPublicacion=findViewById<ImageButton>(R.id.FlechitaPublicacion)
        val imgBtnDescargarArchivo=findViewById<ImageButton>(R.id.imgBtnDescargarArchivo)

        rvComentarios=findViewById<RecyclerView>(R.id.ComentariosPublicacion)
        rvComentarios.layoutManager=LinearLayoutManager(this)
        rvComentarios.setHasFixedSize(true)
        //rvComentarios.adapter=comentariosAdapter2

        idPublicacion=intent.getStringExtra("idPublicacion")
        ObtenerGrupoUsuario()

        ImgPublicacion.setOnClickListener {
            Log.d("Publicacion", "img publicación: "+publicacion?.getUrl().toString())
            if(!publicacion?.getUrl().toString().isEmpty()){
                val urlimg= publicacion?.getUrl()
                val request= DownloadManager.Request(Uri.parse(urlimg))
                    .setTitle(urlimg)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                val dm=getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
            }
        }
        imgBtnDescargarArchivo.setOnClickListener {
            Log.d("Publicacion", "img publicación: "+publicacion?.getUrl().toString())
            if(!publicacion?.getUrl().toString().isEmpty()){
                val urlimg= publicacion?.getUrl()
                val request= DownloadManager.Request(Uri.parse(urlimg))
                    .setTitle(urlimg)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                val dm=getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
            }
        }


        FlechitaPublicacion.setOnClickListener {
             ComentarioPublicacion=findViewById<EditText>(R.id.ComentarioPublicacion).text.toString()
            Log.d("Comentario", "Alumno Comenta: "+usuarioComenta)
            print(usuarioComenta)
            comentarPublicacion(grupo?.getUID().toString(),
                idPublicacion.toString(), ComentarioPublicacion!!, usuarioComenta!!
            )
        }
        comentariosList= arrayListOf<Comentario>()
        Log.d("idPublicacion", idPublicacion.toString())

    }

    private fun ObtenerGrupoUsuario(){
        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (grupoSnapshot in snapshot.children){
                        for (alumnoSnapshot in grupoSnapshot.child("alumnosGrupo").children){
                            val alumno: Users?=alumnoSnapshot.getValue(Users::class.java)
                            if (alumno!!.getUID()==firebaseUser!!.uid){
                                grupo=grupoSnapshot.getValue(Grupo::class.java)
                                obtenerPublicacion()
                                obtenerUsuarioComenta(firebaseUser!!.uid)
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun obtenerPublicacion(){
        groupRef.child(grupo!!.getUID().toString())
            .child("Publicaciones")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (publicacionSnapshot in snapshot.children){
                            val publicacionaux:com.example.POI.ModelClasses.Publicacion?=publicacionSnapshot.getValue(com.example.POI.ModelClasses.Publicacion::class.java)
                            if (idPublicacion==publicacionaux!!.getUID()){
                                publicacion=publicacionaux
                                TxtUsernamePublicacion.text= publicacion?.getUserPublicacion()?.getUsername()
                                Picasso.get().load(publicacion!!.getUserPublicacion()?.getProfile()).into(ProfileImgPublicacion)
                                CuerpoPublicacion.text=publicacion?.getCuerpo()
                                if (publicacion!!.getUrl()?.isEmpty() != true){
                                    //ImgPublicacion.visibility= View.VISIBLE
                                    //Picasso.get().load(publicacion!!.getUrl()).into(ImgPublicacion)
                                    imgBtnDescargarArchivo.visibility=View.VISIBLE
                                }

                                val dateFormater= SimpleDateFormat("dd/MM/yyyy - HH:mm:SS", Locale.getDefault())
                                val fecha=dateFormater?.format(publicacion?.getTimeStamp() as Long)
                                publicacion?.setTimeStamp(fecha!!)
                                FechaPublicacion.text= publicacion!!.getTimeStamp() as CharSequence?
                                getComentarios()
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    private fun obtenerUsuarioComenta(idUsuario:String) {
        groupRef.child(grupo!!.getUID().toString()).child("alumnosGrupo")
            .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (alumnoSnapshot in snapshot.children){
                        val alumno:Users?=alumnoSnapshot.getValue(Users::class.java)
                        if (alumno!!.getUID().toString()==idUsuario){
                            usuarioComenta=alumno
                            Log.d("Comentario", "Funcion alumno: "+usuarioComenta)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun comentarPublicacion(idGrupo:String,idPublicacion: String,
                                    ComentarioPublicacion:String,userComenta:Users){
        val comentarioid=groupRef
            .child(idGrupo)
            .child("Publicaciones")
            .child(idPublicacion)
            .child("Comentarios").push()
        val comentario=Comentario(
            comentarioid.key.toString(),ComentarioPublicacion,"",
            ServerValue.TIMESTAMP,userComenta)
        comentarioid.setValue(comentario)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //Se crea la recompensa
                    recompensasRef.child("Comentar")
                        .child(userComenta.getUID().toString())
                        .setValue(userComenta)
                    Log.d("Comentario","exito")
                    Handler().postDelayed({
                        Toast.makeText(this,"Comentario agregado con exito",
                            Toast.LENGTH_LONG).show()
                    },500)
                }
            }

        Log.d("Publicacion", "idpublicacion a comentar: "+idPublicacion)
        Log.d("Publicacion", "comentario texto: "+ComentarioPublicacion)

    }

    private fun getComentarios(){
        comentariosList.clear()
        groupRef.child(grupo?.getUID().toString()).child("Publicaciones")
            .child(publicacion?.getUID().toString()).child("Comentarios")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (comentarioSnapshot in snapshot.children){
                            val comentario:Comentario?=comentarioSnapshot.getValue(Comentario::class.java)
                            comentariosList.add(comentario!!)
                        }
                        rvComentarios.adapter=ComentariosAdapter2(comentariosList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}