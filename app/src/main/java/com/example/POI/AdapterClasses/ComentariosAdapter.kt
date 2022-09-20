package com.example.POI.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.R
import org.w3c.dom.Text

class ComentariosAdapter:RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder>() {

    class ComentarioViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    //que diseño va a tener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val miview=LayoutInflater.from(parent.context)
            .inflate(R.layout.comentariotuptm,parent,false)
        return ComentarioViewHolder(miview)
    }

    //que va a mostrar
    //se le pasa la info a la tarjeta
    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val tvnombre=holder.itemView.findViewById<TextView>(R.id.TxtUsernameComentario)
        val tvcomentario=holder.itemView.findViewById<TextView>(R.id.TxtComentario)
        tvnombre.text=when(position){
            0->"us0"
            1->"us1"
            2->"us2"
            3->"us3"
            else -> "us"
        }
    }

    //cuantos elementos va a haber
    override fun getItemCount(): Int {
        return 10
    }


}