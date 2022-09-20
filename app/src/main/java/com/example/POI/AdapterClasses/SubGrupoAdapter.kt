package com.example.POI.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.MessageChatActivityGroup
import com.example.POI.ModelClasses.SubGrupo
import com.example.POI.R

class SubGrupoAdapter(
    mContext: Context,
    private val listSubGrupos:ArrayList<SubGrupo>
):RecyclerView.Adapter<SubGrupoAdapter.MyViewHolder>() {
    private val mContext: Context
    init {
        this.mContext=mContext
        //this.mGrupos=mGrupos
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNombreGrupoLista:TextView=itemView.findViewById(R.id.txtNombreGrupoLista)

    }

    //que plantilla se va a usar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.grupo_lista,parent,false)
        return MyViewHolder(itemView)
    }

    //que va a llevar cada objeto/item de esa plantilla
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.txtNombreGrupoLista.text=listSubGrupos[position].getSubgrupoName()
        holder.itemView.setOnClickListener {
            val intent= Intent(mContext, MessageChatActivityGroup::class.java)
            intent.putExtra("visit_id",listSubGrupos[position].getUID())
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listSubGrupos.size
    }

}