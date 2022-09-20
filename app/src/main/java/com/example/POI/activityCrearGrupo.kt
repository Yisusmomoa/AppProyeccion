package com.example.POI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.AlumnoGrupoAdapter
import com.example.POI.Interface.ClickInterface
import com.example.POI.ModelClasses.Grupo
import com.example.POI.ModelClasses.Publicacion
import com.example.POI.ModelClasses.SubGrupo
import com.example.POI.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_crear_grupo.*

class activityCrearGrupo : AppCompatActivity(){

    //referencia a la base de datos
    var database=FirebaseDatabase.getInstance()
    //crear una ramita en la bd que se llamará groups
    val groupRef=database.getReference("groups")
    var mGroupsAlumnos:List<Users>?=null
    var mGroupsSubGrupos:List<SubGrupo>?=null
    var idGrupo:String?=""
    private lateinit var rvAlumnosGrupo:RecyclerView
    private lateinit var listaAlumnos:ArrayList<Users>
    private var listaAlumnosNuevoGrupo= arrayListOf<Users>()
    private var firebaseUser= FirebaseAuth.getInstance().currentUser
    private var recompensasRef=database.getReference("Recompensas")

    lateinit var adapteralu:AlumnoGrupoAdapter
    private var userCreaGrupo:Users?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_grupo)

        rvAlumnosGrupo=findViewById(R.id.rvAlumnosGrupo)
        rvAlumnosGrupo.layoutManager=LinearLayoutManager(this)
        rvAlumnosGrupo.setHasFixedSize(true)

        //rvAlumnosGrupo.adapter=AlumnoGrupoAdapter()
        idGrupo=intent.getStringExtra("idGrupo")
        listaAlumnos= arrayListOf<Users>()
        getAlumnos()
        val spinerGrupos=findViewById<Spinner>(R.id.ComboGruposCarreras)

        Log.d("Grupo", "idGrupo sub"+idGrupo)
        Btn_CrearGrupo.setOnClickListener {
            val groupName:String=findViewById<EditText>(R.id.txt_NombreGrupo).text.toString()
            if (groupName==""){
                Toast.makeText(this, "No debes dejar ningún campo vacío", Toast.LENGTH_SHORT).show()
            }
            else{
                listaAlumnosNuevoGrupo= adapteralu?.regresarAlumnosSelecc() as ArrayList<Users>
                listaAlumnosNuevoGrupo.add(userCreaGrupo!!)

                val subgrupoaux=groupRef.child(idGrupo.toString())
                    .child("SubGrupos")
                    .push()
                var subGrupo:SubGrupo=SubGrupo(
                    "",groupName,
                    ServerValue.TIMESTAMP,listaAlumnosNuevoGrupo)
                subGrupo.setUID(subgrupoaux.key?:"")


                subgrupoaux.setValue(subGrupo)
                    .addOnCompleteListener {
                        it->
                        if (it.isSuccessful){
                            //Se crea la recompensa
                            recompensasRef.child("CrearGrupo")
                                .child(userCreaGrupo!!.getUID().toString())
                                .setValue(userCreaGrupo)
                            Toast.makeText(this, "Grupo Creado con exito",
                                Toast.LENGTH_LONG).show()
                            Handler().postDelayed({
                                val VentanaHome: Intent = Intent(applicationContext,Home::class.java)
                                startActivity(VentanaHome)
                            },500)
                        }
                    }
                /*for (alumno in listaAlumnosNuevoGrupo){
                    Log.d("Inscribir", alumno.getUID().toString())
                }*/


                /*mGroupsSubGrupos=ArrayList()
                mGroupsAlumnos=ArrayList()
                val grupoFirebase=groupRef.push()
                txt_NombreGrupo.text.clear()

                val grupo=Grupo("",groupName,ServerValue.TIMESTAMP )
                grupo.setUID(grupoFirebase.key?:"")
                grupoFirebase.setValue(grupo)*/

            }
        }

    }

    private fun getAlumnos(){
        listaAlumnos.clear()

        groupRef.child(idGrupo!!).child("alumnosGrupo")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (alumnoSnapshot in snapshot.children){
                        val alumnoAux: Users? = alumnoSnapshot.getValue(Users::class.java)
                        if (firebaseUser!!.uid!= alumnoAux!!.getUID()) {
                            listaAlumnos.add(alumnoAux!!)
                        }
                        else{
                            userCreaGrupo=alumnoAux
                        }
                    }
                    adapteralu= AlumnoGrupoAdapter(listaAlumnos)
                    rvAlumnosGrupo.adapter=adapteralu
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }



}