package com.example.POI

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.example.POI.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main_email.*
import kotlinx.android.synthetic.main.activity_main_email.view.*


class Activity_Main_Email : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_email)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main_email)

        findViewById<Button>(R.id.Send).setOnClickListener{

            val para = txtPara.text.toString()
            val asunto = txtAsunto.text.toString()
             val mensaje = mensajes.text.toString()

            val correos = para.split(",".toRegex()).toTypedArray()

           if(para.isNotEmpty() && asunto.isNotEmpty() && mensaje.isNotEmpty()){
               val intent = Intent(Intent.ACTION_SENDTO).apply {
                   data = Uri.parse("mailto:")
                   putExtra(Intent.EXTRA_EMAIL,correos)
                   putExtra(Intent.EXTRA_SUBJECT,asunto)
                   putExtra(Intent.EXTRA_TEXT,mensaje)
               }

                   val intentcorreo = Intent.createChooser(intent,null)
                   startActivity(intentcorreo)


           }else{
               Toast.makeText(this,"Debes llenar todos los campos primero",Toast.LENGTH_SHORT).show()
           }

        }














        /////////////////////////////////////////////







        ///////////////////////////////////////////////////
    }
}