package com.example.POI.AdapterClasses

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.MessageChatActivity
import com.example.POI.ModelClasses.Grupo
import com.example.POI.ModelClasses.Publicacion
import com.example.POI.R
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//val ListaPublicaciones:List<Publicacion>
class PublicacionAdapter(mContext: Context,
                         private var listaPublicaciones: ArrayList<Publicacion>)
    :RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder>() {

    private val mContext: Context
    init {
        this.mContext=mContext
        //this.mGrupos=mGrupos
    }
    inner class PublicacionViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    //cuantos elementos va a haber
    override fun getItemCount(): Int {
        return listaPublicaciones.size
    }

    //como se verá cada uno, cada elemento, la tarjeta
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        //esta linea regresa un view que es un xml
        val miView=LayoutInflater.from(parent.context)
            .inflate(R.layout.publicacion_item,parent,false)
        return PublicacionViewHolder(miView)
    }

    //que información va a mostrar cada elemento
    //pasar la info a la tarjeta
    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {

        //val publicacionAux:Publicacion=listaPublicaciones[position]

        val TxtUsernamePublicacion= holder.itemView.findViewById<TextView>(R.id.TxtUsernamePublicacion)
        val CuerpoPublicacion=holder.itemView.findViewById<TextView>(R.id.CuerpoPublicacion)
        val ImgPublicacion=holder.itemView.findViewById<ImageView>(R.id.ImgPublicacion)
        val FechaPublicacion=holder.itemView.findViewById<TextView>(R.id.FechaPublicacion)


        //TxtUsernamePublicacion.text=ListaPublicaciones[position].getUserPublicacion()?.getUsername()
        TxtUsernamePublicacion.text=listaPublicaciones[position].getUserPublicacion()!!.getUsername()
        CuerpoPublicacion.text=listaPublicaciones[position].getCuerpo()
        if (listaPublicaciones[position].getUrl()?.isEmpty() != true){
            ImgPublicacion.visibility=View.VISIBLE
            Picasso.get().load(listaPublicaciones[position].getUrl()).into(ImgPublicacion)
        }


        try {
            //java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Long
            val dateFormater= SimpleDateFormat("dd/MM/yyyy - HH:mm:SS", Locale.getDefault())
            val fecha=dateFormater?.format(listaPublicaciones[position]?.getTimeStamp() as Long)
            listaPublicaciones[position]?.setTimeStamp(fecha!!)
            FechaPublicacion.text=listaPublicaciones[position].getTimeStamp() as CharSequence?
            //java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Long
        }
        catch (e :Exception){
            Log.d("Exception", "Exepction fecha: "+e.message)
        }

        holder.itemView.setOnClickListener {
            val intent=Intent(mContext,com.example.POI.Publicacion::class.java)
            intent.putExtra("idPublicacion",listaPublicaciones[position].getUID())

            mContext.startActivity(intent)
        }
        /*val intent= Intent(mContext, MessageChatActivity::class.java)
        intent.putExtra("visit_id",user.getUID())
        mContext.startActivity(intent)*/

    }


}