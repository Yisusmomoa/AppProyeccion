package com.example.POI

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.example.POI.ModelClasses.Grupo
import com.example.POI.ModelClasses.Publicacion
import com.example.POI.ModelClasses.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_crear_publicacion.*

class CrearPublicacionActivity : AppCompatActivity() {
    //saber que grupo es
        private val database= FirebaseDatabase.getInstance()
        private var refGroups =database.getReference("groups")
        private var grupo:Grupo?=null
        private var usPublication:Users?=null
        private var firebaseUser= FirebaseAuth.getInstance().currentUser
        private var recompensasRef=database.getReference("Recompensas")

    //saber que grupo es

    lateinit var fileUri:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_publicacion)
        val btnPublicar=findViewById<Button>(R.id.btnPublicar)

        val ImgPublicacion=findViewById<ImageView>(R.id.ImgPublicacion)
        val txtRutaArchivo=findViewById<TextView>(R.id.txtRutaArchivo)
        val ImgButton=findViewById<ImageButton>(R.id.ImgButton)
        EncontrarGrupo()
        ImgButton.setOnClickListener{
            /*var intent= Intent()
            intent=getFileChooserIntentForImageAndPdf()*/
            val intent= Intent()
            intent.type="image/*"
            intent.action= Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Select file"),1)
        }
        FileButton.setOnClickListener {
            val intent= Intent()
            intent.type="*/*"
            intent.action= Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Select file"),2)

        }
        btnPublicar.setOnClickListener {
            val TituloPublicacion=findViewById<EditText>(R.id.TituloPublicacion).text.toString()
            val CuerpoPublicacion=findViewById<EditText>(R.id.CuerpoPublicacion).text.toString()
            if (TituloPublicacion=="" || CuerpoPublicacion==""){
                Toast.makeText(this, "No debes dejar ningún campo vacío", Toast.LENGTH_SHORT).show()
            }
            else{
                val storageReference=FirebaseStorage.getInstance()
                    .reference.child("Posts").child("Img")
                //id grupo
                var idgrupo:String?=grupo?.getUID()
                Log.d("idGrupo","IdGrupo: "+idgrupo)
                //id grupo

                val publicacionaux=refGroups
                    .child(idgrupo.toString())
                    .child("Publicaciones").push()

                //id publicacion
                val idpublicacion=publicacionaux.key
                //id publicacion
                val filePath=storageReference.child("$idpublicacion")

                var uploadTask:StorageTask<*>
                uploadTask=filePath.putFile(fileUri!!)
                uploadTask.continueWithTask<Uri?>(Continuation<UploadTask
                .TaskSnapshot,Task<Uri>> { task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation filePath.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val downloadUrl=task.result
                        val url=downloadUrl.toString()
                        var publicacionUser=Publicacion(publicacionaux.key?:"",
                            TituloPublicacion,
                            CuerpoPublicacion,url,ServerValue.TIMESTAMP,usPublication)
                        /*
                        Log.d("publicacion", "Publicacion: "+publicacionaux)
                    publicacionUser?.setUID(publicacionaux.key?:"")
                        Log.d("publicacion", "Publicacion id: "+publicacionUser)
                    publicacionUser?.setTitulo(TituloPublicacion)
                        Log.d("publicacion", "Publicacion titulo: "+publicacionUser)
                    publicacionUser?.setCuerpo(CuerpoPublicacion)
                        Log.d("publicacion", "Publicacion cuerpo: "+publicacionUser)
                    publicacionUser?.setUrl("")
                        Log.d("publicacion", "Publicacion url: "+publicacionUser)
                    publicacionUser?.setTimeStamp(ServerValue.TIMESTAMP)
                        Log.d("publicacion", "Publicacion tiempo: "+publicacionUser)
                    publicacionUser?.setUserPublicacion(usPublication)
                        Log.d("publicacion", "Publicacion usuario: "+publicacionUser)*/
                        publicacionaux.setValue(publicacionUser)
                            .addOnCompleteListener {
                                it ->
                                if (it.isSuccessful){
                                    //Se crea la recompensa
                                    recompensasRef.child("CrearPublicacion")
                                        .child(usPublication!!.getUID().toString())
                                        .setValue(usPublication)
                                    Toast.makeText(this, "Publicacion " +
                                            "creada con exito",
                                        Toast.LENGTH_LONG).show()
                                    Handler().postDelayed({
                                        val VentanaHome: Intent = Intent(applicationContext,Home::class.java)
                                        startActivity(VentanaHome)
                                    },500)
                                }
                            }
                        Log.d("publicacion", "Publicacion: "+publicacionaux)
                    }

                }

            }
        }

    }
    fun EncontrarGrupo(){
        refGroups.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (grupoSnapShot in snapshot.children){
                        for (alumnosnapShot in grupoSnapShot.child("alumnosGrupo").children){
                            val alumno:Users?=alumnosnapShot.getValue(Users::class.java)
                            if (alumno!!.getUID()==firebaseUser!!.uid){
                                //si esta en alguno de los grupos, lo añado a la lista de grupos
                                grupo=grupoSnapShot.getValue(Grupo::class.java)
                                usPublication=alumno
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun getFileChooserIntentForImageAndPdf(): Intent {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*|application/pdf")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        return intent
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && data!=null){
            val progressBar= ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait.....")
            progressBar.show()
            fileUri=data.data!! // en el celular
            Log.d("img", fileUri.toString())
            progressBar.dismiss()
            if (requestCode==1){
                var bitmap:Bitmap=MediaStore.Images.Media.getBitmap(contentResolver,fileUri)
                ImgPublicacion.setImageBitmap(bitmap)
            }
            if (requestCode==2){
                txtRutaArchivo.text=fileUri.toString()
                txtRutaArchivo.visibility=View.VISIBLE
            }

        }
    }
}