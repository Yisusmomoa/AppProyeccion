package com.example.POI.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.POI.ModelClasses.Comentario
import com.example.POI.ModelClasses.Grupo
import com.example.POI.ModelClasses.Publicacion
import com.example.POI.ModelClasses.Users
import com.example.POI.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.protobuf.Value
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var usersReference:DatabaseReference?=null
    private val database = FirebaseDatabase.getInstance() //primero obtener la referencia a la base de datos
    //esta representando nuestra base de datos, con esto podemos hacer un insert, update, etc
    private var groupRef = database.getReference("groups")
    var firebaseUser:FirebaseUser?=null
    private val RequestCode=438
    private var imageUri: Uri?=null
    private var storageRef:StorageReference?=null
    private var coverChecker:String?=null
    private var usuarioAEditar:Users?=null
    private var grupo: Grupo?=null
    private lateinit var listaPublicaciones:ArrayList<Publicacion>
    private lateinit var comentariosList:ArrayList<Comentario>
    /*
    *
    *
1
rules_version = '2';
2
service firebase.storage {
3
  match /b/{bucket}/o {
4
    match /{allPaths=**} {
5
      allow read, write: if false;
6
    }
7
  }
8
}

    * */


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
        val view= inflater.inflate(R.layout.fragment_settings, container, false)

        listaPublicaciones= arrayListOf<Publicacion>()
        comentariosList= arrayListOf<Comentario>()

        firebaseUser=FirebaseAuth.getInstance().currentUser
        usersReference=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef=FirebaseStorage.getInstance().reference.child("User Images")

        usersReference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    ObtenerGrupoUsuario()
                    Log.d("Grupoeditar","Grupo usuario: "+grupo?.getUID().toString())

                    for (item in listaPublicaciones){
                        Log.d("Publicacioneseditar","Publicaciones: "+item.getUID())
                    }

                    for (item in comentariosList){
                        Log.d("Comentarioseditar", "Comentarios: "+item)
                    }
                    val user:Users?=p0.getValue(Users::class.java)
                    if (context!=null){
                        usuarioAEditar=user
                        Log.d("UsuarioEditar", "Usuario a editar"+usuarioAEditar)


                        view.username_settings.text=user!!.getUsername()
                        Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        view.profile_image_settings.setOnClickListener {
            pickImage()
        }

        return view
    }

    private fun pickImage() {
        val intent=Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RequestCode&&resultCode==Activity.RESULT_OK && data!!.data != null){
            imageUri=data.data
            Toast.makeText(context,"uploading......", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait.....")
        progressBar.show()

        if(imageUri!=null){
            val fileRef=storageRef!!
                .child(System.currentTimeMillis().toString()+".jpg")

            var uploadTask:StorageTask<*>
            uploadTask=fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener {
                task->
                if (task.isSuccessful){
                    //aquí se actualiza la foto
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()
                    val mapProfileImg=HashMap<String,Any>()
                    mapProfileImg["profile"]=url
                    usersReference!!.updateChildren(mapProfileImg)
                    groupRef.child(grupo!!.getUID().toString())
                        .child("alumnosGrupo")
                        .child(firebaseUser!!.uid)
                        .updateChildren(mapProfileImg)

                    if(listaPublicaciones.size>0){
                        for (publicacion in listaPublicaciones){
                            for (comentario in publicacion.comentarios){
                                if (comentario.getUserComentario()?.getUID().toString()==firebaseUser!!.uid){
                                    groupRef.child(grupo!!.getUID().toString())
                                        .child("Publicaciones")
                                        .child(publicacion.getUID().toString())
                                        .child("Comentarios")
                                        .child(comentario.getUID().toString())
                                        .child("userComentario")
                                        .updateChildren(mapProfileImg)
                                }
                            }
                            if (publicacion.getUserPublicacion()?.getUID()==firebaseUser!!.uid){
                                groupRef.child(grupo!!.getUID().toString())
                                    .child("Publicaciones")
                                    .child(publicacion.getUID().toString())
                                    .child("userPublicacion")
                                    .updateChildren(mapProfileImg)
                            }
                        }
                        /*
                        for (publicacion in listaPublicaciones){
                            groupRef.child(grupo!!.getUID().toString())
                                .child("Publicaciones")
                                .child(publicacion.getUID().toString())
                                .child("userPublicacion")
                                .updateChildren(mapProfileImg)

                            for (comentario in comentariosList){
                                groupRef.child(grupo!!.getUID().toString())
                                    .child("Publicaciones")
                                    .child(publicacion.getUID().toString())
                                    .child("Comentarios")
                                    .child(comentario.getUID().toString())
                                    .child("userComentario")
                                    .updateChildren(mapProfileImg)
                            }
                        }*/
                    }
                    else{
                        for (publicacion in listaPublicaciones){
                            for (comentario in publicacion.comentarios){
                                if (comentario.getUserComentario()?.getUID().toString()==firebaseUser!!.uid){
                                    groupRef.child(grupo!!.getUID().toString())
                                        .child("Publicaciones")
                                        .child(publicacion.getUID().toString())
                                        .child("Comentarios")
                                        .child(comentario.getUID().toString())
                                        .child("userComentario")
                                        .updateChildren(mapProfileImg)
                                }
                            }
                        }


                       /*for (publicacion in listaPublicaciones){
                           for (comentario in comentariosList){
                               if(comentario.getUserComentario()?.getUID()==firebaseUser!!.uid){
                                   groupRef.child(grupo!!.getUID().toString())
                                       .child("Publicaciones")
                                       .child(publicacion.getUID().toString())
                                       .child("Comentarios")
                                       .child(comentario.getUID().toString())
                                       .child("userComentario")
                                       .updateChildren(mapProfileImg)
                               }

                           }
                       }*/
                    }

                    progressBar.dismiss()
                }
            }
        }
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
                                ObtenerPublicaciones()
                                if (listaPublicaciones.size>0){
                                    obtenerComentarios()
                                }
                                else{
                                    obtenerTodasLasPublicaciones()

                                }

                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun obtenerTodasLasPublicaciones() {
        listaPublicaciones.clear()
        groupRef.child(grupo!!.getUID().toString())
            .child("Publicaciones")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (snapshotpublicacion in snapshot.children){
                            val publicacion: Publicacion?=snapshotpublicacion.getValue(Publicacion::class.java)
                            for (comentariopublicacion in snapshotpublicacion.child("Comentarios").children){
                                val comentarioaux: Comentario? =comentariopublicacion.getValue(Comentario::class.java)
                                publicacion?.comentarios?.add(comentarioaux!!)
                            }
                            listaPublicaciones.add(publicacion!!)
                        }
                        //obtenerComentariosSinPublicacionesUsuario()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })


    }

    private fun obtenerComentariosSinPublicacionesUsuario(){
        for (publicacion in listaPublicaciones){
            groupRef.child(grupo!!.getUID().toString())
                .child("Publicaciones")
                .child(publicacion.getUID().toString())
                .child("Comentarios")
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            for (comentarioSnapshot in snapshot.children){
                                val comentario:Comentario?=comentarioSnapshot.getValue(Comentario::class.java)
                                if (comentario?.getUserComentario()?.getUID().toString()==firebaseUser!!.uid){
                                    comentariosList.add(comentario!!)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    private fun ObtenerPublicaciones(){
        listaPublicaciones.clear()
        groupRef.child(grupo!!.getUID().toString())
            .child("Publicaciones")
            .addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (publicacionSnapshot in snapshot.children){
                        val publicacion: Publicacion? =publicacionSnapshot.getValue(Publicacion::class.java)
                        if (publicacion?.getUserPublicacion()?.getUID() ==firebaseUser!!.uid){
                            listaPublicaciones.add(publicacion)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun obtenerComentarios(){
        comentariosList.clear()
        for (publicacion in listaPublicaciones){
            groupRef.child(grupo?.getUID().toString()).child("Publicaciones")
                .child(publicacion.getUID().toString()).child("Comentarios")
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (comentarioSnapshot in snapshot.children){
                            val comentario: Comentario? =comentarioSnapshot.getValue(Comentario::class.java)
                            if (comentario!!.getUserComentario()?.getUID() ==firebaseUser!!.uid){
                                comentariosList.add(comentario)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }


    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}