package com.example.POI

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.POI.ModelClasses.Grupo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*


class activity_Register : AppCompatActivity(){
    private  lateinit var mAuth: FirebaseAuth
    private var database=FirebaseDatabase.getInstance()
    private lateinit var refUsers:DatabaseReference
    private var refGroups:DatabaseReference=database.getReference("ListaGruposAlumnos")
    private var firebaseUserID:String=""
    private var recompensasRef=database.getReference("Recompensas")

    var mGroups:List<Grupo>?=null
    private var listagrupos= mutableListOf<Grupo>()
    var idgrupo:String=""
    var nombregrupo:String=""

    var firebaseUser: FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth= FirebaseAuth.getInstance()
        val btnsignup=findViewById<Button>(R.id.btn_signup)
        val spinner=findViewById<Spinner>(R.id.ComboGruposCarreras)

        var spinnerGrupos:Spinner=findViewById(R.id.ComboGruposCarreras)
        var txt_NombreGrupo:TextView=findViewById(R.id.txtGrupoSeleccionado)
        loadGroups()

        ComboGruposCarreras.onItemSelectedListener=object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0?.getItemAtPosition(p2).toString()
                txt_NombreGrupo?.text=listagrupos[p2].getGroupName()
                idgrupo=listagrupos[p2].getUID().toString()
                nombregrupo=listagrupos[p2].getGroupName().toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }


        btnsignup.setOnClickListener {
            registerUser()
            /*val intent= Intent(this@activity_Register, Home::class.java)
            startActivity(intent)
            finish()*/
        }


    }

    private fun loadGroups(){
        //referencia a la base de datos
        var database=FirebaseDatabase.getInstance()
        var adaptadorGrupos=ArrayAdapter<Grupo>(this,android.R.layout.simple_spinner_item,listagrupos)
        adaptadorGrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //crear una ramita en la bd que se llamará groups
        val groupRef=database.getReference("groups")

        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapShot in p0.children){
                    var id:String=snapShot.key.toString()
                    var nombre:String=snapShot.child("groupName").getValue().toString()
                    //(mGroups as ArrayList<Grupo>).add(Grupo(id,nombre))
                    listagrupos.add(Grupo(id,nombre))
                }
                ComboGruposCarreras.adapter=adaptadorGrupos
            }
            override fun onCancelled(error: DatabaseError) {
            }


        })



    }

    private fun registerUser(){
        //obtengo el texto de los edit text
        val username:String=findViewById<EditText>(R.id.editTextUserName).text.toString()
        val email:String=findViewById<EditText>(R.id.editTextEmailAddress).text.toString()
        val pass:String=findViewById<EditText>(R.id.txtAsunto).text.toString()

        if(username=="" || email=="" || pass==""){
            Toast.makeText(this, "No debes de dejar ningún campo vacío", Toast.LENGTH_SHORT).show()
        }
        else{
            mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        firebaseUserID=mAuth.currentUser!!.uid
                        refUsers=FirebaseDatabase.getInstance().reference
                            .child("Users").child(firebaseUserID)
                        val userHashMap=HashMap<String,Any>()
                        userHashMap["uid"]=firebaseUserID
                        userHashMap["username"]=username
                        userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/messengerpoi2.appspot.com/o/profile.png?alt=media&token=dcbc6c9e-7bdb-4453-afac-4d9e6b913c38"
                        userHashMap["status"]="offline"
                        userHashMap["search"]=username.toLowerCase()
                        userHashMap["encrypt"]=true

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener {
                                task->
                                if (task.isSuccessful){
                                    //Se crea la recompensa
                                    recompensasRef.child("Registrado")
                                        .child(firebaseUserID)
                                        .setValue(userHashMap)
                                    Toast.makeText(this, "Registrado con exito",
                                        Toast.LENGTH_LONG).show()
                                    refGroups=database.getReference()
                                        .child("groups")
                                        .child(idgrupo)
                                        .child("alumnosGrupo")
                                        .child(firebaseUserID)
                                    //val alumnolistagrupo=refGroups.push()
                                    refGroups.setValue(userHashMap)
                                    Handler().postDelayed({
                                        val intent=Intent(this@activity_Register, Home::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    },500)

                                }
                            }





                        /*
                                   * private var refGroups:DatabaseReference=database
                                   * .getReference("ListaGruposAlumnos")
                                   * */

                       /* refGroups.addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(!snapshot.exists()){
                                    refGroups.push()
                                    refGroups.setValue(idgrupo,nombregrupo)

                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                        refGroups.child(idgrupo).child("alumnos").push()
                        refGroups.child(idgrupo).child("alumnos").setValue(firebaseUserID)
                        refGroups.child(idgrupo).child("alumnos").child("id").push()
                        refGroups.child(idgrupo).child("alumnos")
                            .child(firebaseUserID).setValue(userHashMap)*/





                    }
                    else{
                        Toast.makeText(this, "Error: ${task.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

}

