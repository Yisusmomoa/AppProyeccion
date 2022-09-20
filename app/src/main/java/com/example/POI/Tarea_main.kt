package com.example.POI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.TareaAdapter
import com.example.POI.ModelClasses.Tarea
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Tarea_main : AppCompatActivity() {
    var idGrupo:String?=""
    lateinit var rvTareas: RecyclerView
    private val database=FirebaseDatabase.getInstance()
    private var groupRef=database.getReference("groups")
    private lateinit var tareasList:ArrayList<Tarea>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea_main)

        //val TareaVer = findViewById<Button>(R.id.btn_tareaver)
        val TareaCrear = findViewById<Button>(R.id.btn_tareacrear)
        rvTareas=findViewById(R.id.rvTareas)
        rvTareas.layoutManager= LinearLayoutManager(this)
        rvTareas.setHasFixedSize(true)

        tareasList= arrayListOf<Tarea>()

        //rvTareas.adapter=TareaAdapter()

        idGrupo=intent.getStringExtra("idGrupo")
        getTareasGroup(idGrupo!!)

        Log.d("Grupo", "idGrupo sub"+idGrupo)

        /*TareaVer.setOnClickListener{
            val VentanaTareaVer: Intent = Intent(applicationContext,TareaVer_main::class.java)
            startActivity(VentanaTareaVer)

        }*/

        TareaCrear.setOnClickListener{
            val VentanaTareaCrear: Intent = Intent(applicationContext,TareaCrear_main::class.java)
            VentanaTareaCrear.putExtra("idGrupo", idGrupo)
            startActivity(VentanaTareaCrear)

        }

    }

    private fun getTareasGroup(idGrupo:String){
        tareasList.clear()
        groupRef.child(idGrupo)
            .child("Tareas")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (tareaSnapshot in snapshot.children){
                            val tarea:Tarea?=tareaSnapshot.getValue(Tarea::class.java)
                            tareasList.add(tarea!!)
                        }
                        rvTareas.adapter=TareaAdapter(this@Tarea_main,tareasList,idGrupo)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}