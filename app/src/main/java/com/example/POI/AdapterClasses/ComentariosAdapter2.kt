package com.example.POI.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.ModelClasses.Comentario
import com.example.POI.R
import com.squareup.picasso.Picasso

class ComentariosAdapter2(
    private val comentariosList: ArrayList<Comentario>
    )
    :RecyclerView.Adapter<ComentariosAdapter2.ComentarioViewHolder>() {

    class ComentarioViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val miview= LayoutInflater.from(parent.context)
            .inflate(R.layout.comentario_item2,parent,false)
        return ComentariosAdapter2.ComentarioViewHolder(miview)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val tvnombre=holder.itemView.findViewById<TextView>(R.id.TxtUsernameComentario)
        val tvcomentario=holder.itemView.findViewById<TextView>(R.id.TxtComentario)
        val ProfileImgComentario=holder.itemView.findViewById<ImageView>(R.id.ProfileImgComentario)

        Picasso.get().load(comentariosList[position].getUserComentario()!!.getProfile()).into(ProfileImgComentario)
        tvnombre.text= comentariosList[position].getUserComentario()!!.getUsername()
        tvcomentario.text=comentariosList[position].getTextoComentario()
        /*tvnombre.text=when(position){
            0->"us0"
            1->"us1"
            2->"us2"
            3->"us3"
            else -> "us"
        }*/
    }

    override fun getItemCount(): Int {
        return comentariosList.size
        //return 10
    }
}