package com.example.POI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.GroupsAdapter
import com.example.POI.AdapterClasses.SubGrupoAdapter
import com.example.POI.ModelClasses.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_crear_grupo.*
import kotlinx.android.synthetic.main.activity_sub_gmain.*
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlin.math.log

class SubG_main : AppCompatActivity() {
    private lateinit var listaGrupos:ArrayList<Grupo>
    private lateinit var listaAlumnos:ArrayList<Users>
    //private var adapterGrupos=GroupsAdapter(listaGrupos)

    private val database = FirebaseDatabase.getInstance() //primero obtener la referencia a la base de datos
    //esta representando nuestra base de datos, con esto podemos hacer un insert, update, etc

    //nos crea una rama llamada chats, donde iran los mensajes
    private val groupRef = database.getReference("groups")
    //private val alumnosGrupo=database.getReference("groups").child("alumnosGrupo")
     //   .child("alumnosGrupo")

    var alumnosGrupoAux:ArrayList<Users>?=null
    var firebaseUser=FirebaseAuth.getInstance().currentUser
    var refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
    var idGrupo:String?=""
    private lateinit var listaSubGrupos:ArrayList<SubGrupo>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_gmain)

        val rvGrupos=findViewById<RecyclerView>(R.id.rvGrupos)
        val rvSubGrupos=findViewById<RecyclerView>(R.id.rvSubGrupos)
        rvGrupos.layoutManager=LinearLayoutManager(this)
        rvGrupos.setHasFixedSize(true)

        rvSubGrupos.layoutManager=LinearLayoutManager(this)
        rvSubGrupos.setHasFixedSize(true)
        //rvSubGrupos.adapter=SubGrupoAdapter()

        listaGrupos= arrayListOf<Grupo>()
        listaAlumnos= arrayListOf<Users>()

        idGrupo=intent.getStringExtra("idGrupo")
        Log.d("Grupo", "idGrupo: "+idGrupo)
        cargarGrupos()
        getSubGrupos()
        //rvGrupos.adapter=adapterGrupos

        val Subgrupo = this.findViewById<ImageButton>(R.id.btn_sgrupo)
        val Chat = this.findViewById<ImageButton>(R.id.btn_chats)
        val Perfil = this.findViewById<ImageButton>(R.id.btn_perfil)
        val Recompensa = this.findViewById<ImageButton>(R.id.btn_recompensa)
        val Tarea = this.findViewById<ImageButton>(R.id.btn_tarea)


/////Boton para mandar a llamar ventana de chats/////

        Chat.setOnClickListener{
            val VentanaChat: Intent = Intent(applicationContext,Chat_main::class.java)
            startActivity(VentanaChat)
        }
/////Boton para mandar a llamar ventana de perfil/////

        Perfil.setOnClickListener{
            val VentanaPerfil: Intent = Intent(applicationContext,Perfil_main::class.java)
            startActivity(VentanaPerfil)
        }
/////Boton para mandar a llamar ventana de perfil/////

        Recompensa.setOnClickListener{
            val VentanaRecompensa: Intent = Intent(applicationContext,Recompensa_main::class.java)
            startActivity(VentanaRecompensa)
        }
/////Boton para mandar a llamar ventana de perfil/////

        Tarea.setOnClickListener{
            val VentanaTarea: Intent = Intent(applicationContext,Tarea_main::class.java)
            startActivity(VentanaTarea)
        }

        btn_CrearGrupo.setOnClickListener {
            val ventanaCrearGrupo:Intent= Intent(this,activityCrearGrupo::class.java)
            ventanaCrearGrupo.putExtra("idGrupo",idGrupo)
            startActivity(ventanaCrearGrupo)
        }
    }

    private fun cargarGrupos(){

        var mGroupsAlumnos:List<Users>?=null
        var hashMapArray = ArrayList<HashMap<String, Any?>>()

        /*groupRef.child("alumnosGrupo").orderByChild("uid").equalTo(firebaseUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (grupoSnapshot in snapshot.children){
                            val grupo: Grupo? =grupoSnapshot.getValue(Grupo::class.java)
                            listaGrupos.add(grupo!!)
                        }
                        rvGrupos.adapter=GroupsAdapter(this@SubG_main,listaGrupos)
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })*/

        /*
        groupRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (p0 in snapshot.children){
                    val grupo: Grupo? = p0.getValue(Grupo::class.java)
                    listaGrupos.add(grupo!!)
                }
                adapterGrupos.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })*/

        listaGrupos.clear()
     //Trae todos los grupos
    groupRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //recorro todos los grupos e instanciar el grupo en el que este el usuario logeado
                    for (grupoSnapshot in snapshot.children){
                        //Log.d("grupo", "onDataChange: ${grupoSnapshot}")
                        //Log.d("Alumnos", "AlumnosGrupo: "+ grupoSnapshot.child("alumnosGrupo"))
                            //recorro el arreglo "alumnosGrupo" de cada grupo buscando si el usuario logeado esta en alguno de ellos
                        for (alumnosnapShot in grupoSnapshot.child("alumnosGrupo").children){
                            val alumno:Users?=alumnosnapShot.getValue(Users::class.java)
                            if (alumno!!.getUID()==firebaseUser!!.uid){
                                //si esta en alguno de los grupos, lo añado a la lista de grupos
                                val grupo: Grupo? =grupoSnapshot.getValue(Grupo::class.java)
                                listaGrupos.add(grupo!!)
                                //(mGroupsAlumnos as ArrayList<Users>).clear()
                            }
                        }

                        //Log.d("Alumnos", "AlumnosGrupo: "+ grupoSnapshot.child("alumnosGrupo").value)
                    }
                    rvGrupos.adapter=GroupsAdapter(this@SubG_main,listaGrupos)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getSubGrupos(){
        listaSubGrupos= arrayListOf<SubGrupo>()
        listaSubGrupos.clear()
        groupRef.child(idGrupo!!)
            .child("SubGrupos")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (subGrupoSnapshot in snapshot.children){
                        val subGrupo:SubGrupo?=subGrupoSnapshot.getValue(SubGrupo::class.java)
                        for (alumnoSubGrupoSnapshot in subGrupoSnapshot.child("alumnosSubGrupo").children){
                            val alumno: Users? =alumnoSubGrupoSnapshot.getValue(Users::class.java)
                            if (alumno!!.getUID().toString()==firebaseUser!!.uid){
                                listaSubGrupos.add(subGrupo!!)
                            }
                        }
                    }
                    rvSubGrupos.adapter=SubGrupoAdapter(this@SubG_main,listaSubGrupos)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}