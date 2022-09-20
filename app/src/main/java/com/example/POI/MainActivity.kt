package com.example.POI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private  lateinit var mAuth: FirebaseAuth

    var firebaseUser: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth= FirebaseAuth.getInstance()//19:49

        val Singup = findViewById<Button>(R.id.btn_send) //login
        val Registrar=findViewById<Button>(R.id.btn_signup)

        Singup.setOnClickListener{
            loginUser()
        }

        Registrar.setOnClickListener {
            val VentanaRegistrar:Intent=Intent(applicationContext,activity_Register::class.java)
            startActivity(VentanaRegistrar)
        }

    }

    private fun loginUser(){
        val email:String=findViewById<EditText>(R.id.txtPara).text.toString()
        val pass:String=findViewById<EditText>(R.id.txtAsunto).text.toString()

        if(email==""||pass==""){
            Toast.makeText(this, "No debes de dejar ningún campo vacío", Toast.LENGTH_SHORT).show()
        }
        else{
            mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener{
                    task->
                    if(task.isSuccessful){
                        val intent=Intent(this@MainActivity, Home::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Error: ${task.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    //cuando se inicie esta pantalla saber si el usuario ya esta logeado o no
    override fun onStart() {
        super.onStart()
        firebaseUser= FirebaseAuth.getInstance().currentUser

        if(firebaseUser!=null){
            val intent=Intent(this@MainActivity, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}