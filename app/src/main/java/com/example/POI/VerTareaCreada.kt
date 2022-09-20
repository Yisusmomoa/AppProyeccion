package com.example.POI

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.POI.ModelClasses.Tarea
import com.example.POI.ModelClasses.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_ver_tarea_creada.*
import org.w3c.dom.Text
import java.lang.Exception
import com.google.android.gms.tasks.Continuation
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class VerTareaCreada : AppCompatActivity() {
    var idTarea:String=""
    var idGrupo:String=""
    var tarea:Tarea?=null
    private var userLogeado:Users?=null

    //referencia a la base de datos
    var database= FirebaseDatabase.getInstance()
    //crear una ramita en la bd que se llamará groups
    val groupRef=database.getReference("groups")


    var uri:Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_tarea_creada)

        idTarea= intent.getStringExtra("idTarea").toString()
        idGrupo= intent.getStringExtra("idGrupo").toString()
        val FechaTarea=findViewById<TextView>(R.id.FechaTarea)
        val DescrTarea=findViewById<TextView>(R.id.DescrTarea)
        val btn_AdjuntarDoc=findViewById<Button>(R.id.btn_AdjuntarDoc)
        val TituloTarea=findViewById<TextView>(R.id.TituloTarea)
        val btnFinalizarTarea=findViewById<Button>(R.id.btnFinalizarTarea)
        getTarea()

        btn_AdjuntarDoc.setOnClickListener {
            it -> val intent=Intent()
            intent.type="*/*"
            //intent.type="image/*" //original
            intent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Pick file"), 438)
        }
        btnFinalizarTarea.setOnClickListener {
            if(uri!=null){
                val storageReference=FirebaseStorage.getInstance()
                    .reference
                    .child("Tareas")
                val ref=FirebaseDatabase.getInstance().reference
                val tareaId=tarea!!.getUID()
                val filePath=storageReference.child("$tareaId")
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
                            task ->
                            if (task.isSuccessful){
                                val downloadUrl=task.result
                                val url=downloadUrl.toString()
                                val mapFileTarea=HashMap<String,Any>()
                                mapFileTarea["url"]=url
                                groupRef.child(idGrupo)
                                    .child("Tareas")
                                    .child(idTarea)
                                    .updateChildren(mapFileTarea)
                            }
                        }
                }
                catch (e:Exception){

                }
            }
            Toast.makeText(this, "Tarea Creada con exito", Toast.LENGTH_LONG).show()

            Handler().postDelayed({
                val intent= Intent(this, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }, 1500)

        }
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
        }
    }
    private fun setDetallesTarea(tarea: Tarea) {
        FechaTarea.text=tarea.getFechaLimite()
        DescrTarea.text=tarea.getDescripcion()
        TituloTarea.text=tarea.getNombreTarea()
    }

    private fun getTarea(){
        groupRef
            .child(idGrupo)
            .child("Tareas")
            .child(idTarea)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    tarea=snapshot.getValue(Tarea::class.java)
                    setDetallesTarea(tarea!!)
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


}