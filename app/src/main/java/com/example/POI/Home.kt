package com.example.POI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.PublicacionAdapter
import com.example.POI.ModelClasses.Grupo
import com.example.POI.ModelClasses.Publicacion
import com.example.POI.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*

@Suppress("LocalVariableName")
class Home : AppCompatActivity() {
    private lateinit var listaPublicaciones:ArrayList<Publicacion>
    //private var adapterPublicaciones:PublicacionAdapter=PublicacionAdapter()

    private val database = FirebaseDatabase.getInstance() //primero obtener la referencia a la base de datos
    //esta representando nuestra base de datos, con esto podemos hacer un insert, update, etc
    var firebaseUser=FirebaseAuth.getInstance().currentUser
    private var groupRef = database.getReference("groups")
    private var groupRef2 = database.getReference("groups")
    var grupo: Grupo?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val Chat = this.findViewById<ImageButton>(R.id.btn_chats)
        val Perfil = this.findViewById<ImageButton>(R.id.btn_perfil)
        val Recompensa = this.findViewById<ImageButton>(R.id.btn_recompensa)
        val Tarea = this.findViewById<ImageButton>(R.id.btn_tarea)
        val Subgrupo = this.findViewById<ImageButton>(R.id.btn_sgrupo)

        val btnLogOut=this.findViewById<Button>(R.id.btnLogOut)

        val rvPublicaciones=findViewById<RecyclerView>(R.id.rvPublicaciones)
        rvPublicaciones.layoutManager= LinearLayoutManager(this)
        listaPublicaciones= arrayListOf<Publicacion>()
       /* val publicacionesHardcode= mutableListOf<Publicacion>()
        for (i in 0..99){
            publicacionesHardcode.add(
                Publicacion()
            )
        }*/
        ObtenerGrupoUsuario()
        //cargarPublicaciones()



        buttonactu.setOnClickListener {
            val crearPublicacion=Intent(this,CrearPublicacionActivity::class.java)
            startActivity(crearPublicacion)

        }

/////Boton para mandar a llamar ventana de subgrupos/////

        Subgrupo.setOnClickListener{

            val VentanaSG = Intent(applicationContext,SubG_main::class.java)
            VentanaSG.putExtra("idGrupo", grupo?.getUID().toString())

            startActivity(VentanaSG)
        }
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
/////Boton para mandar a llamar ventana de Recompensa/////

        Recompensa.setOnClickListener{
            val VentanaRecompensa: Intent = Intent(applicationContext,Recompensa_main::class.java)
            startActivity(VentanaRecompensa)
        }
/////Boton para mandar a llamar ventana de Tarea/////

        Tarea.setOnClickListener{
            val VentanaTarea: Intent = Intent(applicationContext,Tarea_main::class.java)
            VentanaTarea.putExtra("idGrupo", grupo?.getUID().toString())
            startActivity(VentanaTarea)
        }

        btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent=Intent(this@Home, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }
    
    private fun ObtenerGrupoUsuario(){
        //listaPublicaciones.clear()
        groupRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (grupoSnapshot in snapshot.children){
                        for (alumnoSnapshot in grupoSnapshot.child("alumnosGrupo").children){
                            val alumno: Users?=alumnoSnapshot.getValue(Users::class.java)
                            if (alumno!!.getUID()==firebaseUser!!.uid){
                                grupo=grupoSnapshot.getValue(Grupo::class.java)
                                cargarPublicaciones()
                            }
                        }
                    }
                    rvPublicaciones.adapter=PublicacionAdapter(this@Home,listaPublicaciones)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        //cargarPublicaciones()
    }

    private fun cargarPublicaciones(){
        listaPublicaciones.clear()
        groupRef2.child(grupo!!.getUID().toString())
            .child("Publicaciones").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (publicacionSnapshot in snapshot.children){
                            val publicacion:Publicacion?=publicacionSnapshot.getValue(Publicacion::class.java)
                            listaPublicaciones.add(publicacion!!)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


}