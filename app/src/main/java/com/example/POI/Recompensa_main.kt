package com.example.POI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.AdapterClasses.RecompensaAdapter
import com.example.POI.ModelClasses.Recompensa
import com.example.POI.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Recompensa_main : AppCompatActivity() {
    private lateinit var rvRecompensas:RecyclerView
    //referencia a la base de datos
    val database= FirebaseDatabase.getInstance()
    private val firebaseUser= FirebaseAuth.getInstance().currentUser
    private val recompensasRef=database.getReference("Recompensas")

    private lateinit var recompensaList:ArrayList<Recompensa>
    private lateinit var listadetutptm:ArrayList<Users>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recompensa_main)

        recompensaList= arrayListOf<Recompensa>()
        listadetutptm= arrayListOf<Users>()
        getRecompensas(firebaseUser!!.uid)

        rvRecompensas=findViewById<RecyclerView>(R.id.rvRecompensas)
        rvRecompensas.layoutManager=LinearLayoutManager(this)
        rvRecompensas.setHasFixedSize(true)
    }

    fun getRecompensas(idUser:String){
        recompensaList.clear()
        //val tuptmrecompensa:Recompensa= Recompensa()
        //val tuptmrecompensa2:Recompensa=Recompensa("tuptm",listadetutptm)

        //esta variable la creo arriba, pero solo la tengo aquí como referencia para saber hacia
        //donde estoy apuntando
        //private val recompensasRef=database.getReference("Recompensas")
        recompensasRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (recompensaSnapshot in snapshot.children){
                        Log.d("Recompensa", recompensaSnapshot.key.toString())
                        val recompensaAux:Recompensa?=recompensaSnapshot.getValue(Recompensa::class.java)
                        recompensaAux!!.setNombreRecompensa(recompensaSnapshot.key.toString())
                        for (usRecompensaSnapshot in recompensaSnapshot.children){
                            val usuarioAux: Users? =usRecompensaSnapshot.getValue(Users::class.java)
                            if (usuarioAux?.getUID().toString() == firebaseUser?.uid){
                                recompensaList.add(recompensaAux!!)
                            }
                        }
                    }
                    rvRecompensas.adapter=RecompensaAdapter(recompensaList)
                    //rvRecompensas.adapter=RecompensaAdapter()
                }


            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

/*
* if (snapshot.exists()){
                    for (recompensaSnapshot in snapshot.children){
                    * Log.d("Recompensa", recompensaSnapshot.key.toString())
                        val recompensaAux:Recompensa?=recompensaSnapshot.getValue(Recompensa::class.java)
                        for (usRecompensaSnapshot in recompensaSnapshot.children){
                            val usuarioAux: Users? =usRecompensaSnapshot.getValue(Users::class.java)
                            if (usuarioAux?.getUID().toString() == firebaseUser?.uid){
                                recompensaList.add(recompensaAux!!)
                            }
                        }
                    }
                    //rvRecompensas.adapter=RecompensaAdapter(recompensaList)
                    rvRecompensas.adapter=RecompensaAdapter()
                }
*
* */

}