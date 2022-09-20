package com.example.POI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.example.POI.ModelClasses.Tarea
import com.example.POI.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.protobuf.Value

class TareaCrear_main : AppCompatActivity() {
    //referencia a la base de datos
    var database= FirebaseDatabase.getInstance()
    //crear una ramita en la bd que se llamará groups
    val groupRef=database.getReference("groups")
    val firebaseUser=FirebaseAuth.getInstance().currentUser
    private var recompensasRef=database.getReference("Recompensas")

    var idGrupo:String?=""
    var usCreaTarea:Users?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea_crear_main)


        val TareaCrear = findViewById<Button>(R.id.btn_crearTarea)
        val txtNombretarea=findViewById<EditText>(R.id.txtNombretarea)
        val txtDescrTarea=findViewById<EditText>(R.id.txtDescrTarea)
        val FechaLimite=findViewById<DatePicker>(R.id.FechaLimite)
        var formatoFecha:String?=""
        idGrupo=intent.getStringExtra("idGrupo")
        Log.d("Grupo", "idGrupo sub"+idGrupo)
        obtenerUsuarioCreaTarea()


        TareaCrear.setOnClickListener{
            var mes:String?=""
            if (FechaLimite.getMonth()+1<10) {
                mes = "0" + FechaLimite.getMonth().toString()
                formatoFecha=FechaLimite.getDayOfMonth().toString()+"/"+
                        mes+"/"+FechaLimite.getYear().toString()
            }
            else{
                formatoFecha=FechaLimite.getDayOfMonth().toString()+"/"+
                        (FechaLimite.getMonth() + 1)+"/"+FechaLimite.getYear().toString()
            }
            Log.d("DatePicker","Selected Date: "+formatoFecha)
            var tarea:Tarea= Tarea("",txtNombretarea.text.toString(),
                txtDescrTarea.text.toString(),
                ServerValue.TIMESTAMP, formatoFecha!!,
                "",usCreaTarea )

            val idTarea:String=CrearTarea(tarea)
            Handler().postDelayed({
                val VentanaVerTareaCreada: Intent = Intent(applicationContext,VerTareaCreada::class.java)
                VentanaVerTareaCreada.putExtra("idTarea", idTarea)
                VentanaVerTareaCreada.putExtra("idGrupo", idGrupo)
                startActivity(VentanaVerTareaCreada)
            },500)

        }


    }

    fun obtenerUsuarioCreaTarea(){
        groupRef.child(idGrupo.toString())
            .child("alumnosGrupo")
            .child(firebaseUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    usCreaTarea=snapshot.getValue(Users::class.java)
                    Log.d("UsTarea", "${usCreaTarea?.getUID().toString()}," +
                            "${usCreaTarea?.getUsername()} ")
                }
                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
    fun CrearTarea(tarea:Tarea):String{
        val tareaAux=groupRef.child(idGrupo.toString())
            .child("Tareas")
            .push()
        tarea.setUID(tareaAux.key?:"")
        tareaAux.setValue(tarea)
            //Se crea la recompensa
            .addOnCompleteListener {
                    it ->
                if (it.isSuccessful){
                    recompensasRef.child("CreacionTareas")
                        .child(firebaseUser!!.uid)
                        .setValue(usCreaTarea)
                   // Toast.makeText(this, "Tarea Creada con exito", Toast.LENGTH_SHORT).show()

                }
            }
        return tareaAux.key.toString()
    }
}