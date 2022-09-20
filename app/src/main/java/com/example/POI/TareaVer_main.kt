package com.example.POI

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.POI.ModelClasses.Tarea
import com.example.POI.ModelClasses.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.w3c.dom.Text
import java.lang.Exception
import com.google.android.gms.tasks.Continuation

class TareaVer_main : AppCompatActivity() {
    private val database=FirebaseDatabase.getInstance()
    private var groupRef=database.getReference("groups")
    private var tareasRef=database.getReference("TareasEntregadas")
    private var firebaseUser=FirebaseAuth.getInstance().currentUser
    private var recompensasRef=database.getReference("Recompensas")

    private var tarea:Tarea?=null
    private var userLogeado:Users?=null

    var TituloTarea:TextView?=null
    var txtFechaTarea:TextView?=null
    var DescrTarea:TextView?=null
    var txtStatus:TextView?=null
    var txtNombreArchivoTarea:TextView?=null
    var uri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea_ver_main)

        val idTarea=intent.getStringExtra("idTarea")
        val idGrupo=intent.getStringExtra("idGrupo")

        getTarea(idTarea.toString(),idGrupo.toString())
        getUser(firebaseUser!!.uid, idGrupo.toString())

        TituloTarea=findViewById<TextView>(R.id.TituloTarea)
        txtFechaTarea=findViewById<TextView>(R.id.txtFechaTarea)
        DescrTarea=findViewById<TextView>(R.id.DescrTarea)
        txtStatus=findViewById<TextView>(R.id.txtStatus)
        txtNombreArchivoTarea=findViewById(R.id.txtNombreArchivoTarea)


        val btn_tareasubir=findViewById<Button>(R.id.btn_tareasubir)
        val btn_EntregarTarea=findViewById<Button>(R.id.btn_EntregarTarea)
        val imgBtnDescargarArchivo=findViewById<ImageButton>(R.id.imgBtnDescargarArchivo)


        //subir pdf
        btn_tareasubir.setOnClickListener {
                it -> val intent= Intent()
            intent.type="*/*"
            //intent.type="image/*" //original
            intent.action= Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Pick file"),438)

        // startActivityForResult(Intent.createChooser(intent,
             //   "Pick file"), 438)
        }

        //subir firebase
        btn_EntregarTarea.setOnClickListener {

            if(uri!=null){
                val storageReference=FirebaseStorage.getInstance()
                    .reference
                    .child("TareasEntregadas")
                    .child(tarea?.getUID().toString())
                val tareaEntregadaId= userLogeado?.getUID().toString()+"_"+ tarea?.getUID().toString()
                val filePath=storageReference.child("$tareaEntregadaId")
                try {
                    val uploadTask:StorageTask<*>
                    uploadTask=filePath.putFile(uri!!)
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

                        }
                    }
                }
                catch (e: Exception){

                }

            }

            tareasRef.child(tarea?.getUID().toString())
                .child("AlumnosEntrega")
                .child(userLogeado?.getUID().toString())
                .setValue(userLogeado)
                    //se añade la recompensa cuando el usuario entrega la tarea
                //Se crea la recompensa
                .addOnCompleteListener {
                    it ->
                    if (it.isSuccessful){
                        recompensasRef.child("TareaEntregada")
                            .child(userLogeado?.getUID().toString())
                            .setValue(userLogeado)
                        Toast.makeText(this, "Tarea entregada con exito", Toast.LENGTH_LONG).show()

                    }
                }
        }

        imgBtnDescargarArchivo.setOnClickListener {
            if (!tarea?.getUrl().toString().isEmpty()){
                val urlimg= tarea?.getUrl()
                val request= DownloadManager.Request(Uri.parse(urlimg))
                    .setTitle(urlimg)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                val dm=getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
            }
            else{
                Toast.makeText(this,"No hay ningun archivo", Toast.LENGTH_LONG).show()
            }

        }

        Log.d("idTarea", "IdTarea: "+idTarea)
        Log.d("idTarea", "idGrupo"+idGrupo)


    }

    private fun getTarea(idTarea:String,idGrupo:String){
        groupRef.child(idGrupo)
            .child("Tareas")
            .child(idTarea)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        tarea=snapshot.getValue(Tarea::class.java)
                        TituloTarea?.text= tarea!!.getNombreTarea()
                        txtFechaTarea?.text=tarea!!.getFechaLimite()
                        DescrTarea?.text=tarea!!.getDescripcion()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getUser(idUser:String,idGrupo: String){
        groupRef.child(idGrupo)
            .child("alumnosGrupo")
            .child(idUser)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        userLogeado=snapshot.getValue(Users::class.java)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==438 && resultCode== RESULT_OK &&
            data!=null && data.data!=null){
            val progressBar= ProgressDialog(this)
            progressBar.setMessage("File is uploading, please wait.....")
            progressBar.show()
            uri= data.data!!
            progressBar.dismiss()
            txtNombreArchivoTarea?.text=uri.toString()
        }
    }


}