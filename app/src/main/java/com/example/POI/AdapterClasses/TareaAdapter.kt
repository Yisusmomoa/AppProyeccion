package com.example.POI.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.ModelClasses.Tarea
import com.example.POI.R
import com.example.POI.TareaVer_main

class TareaAdapter(
    mContext: Context,
    private val tareaList:ArrayList<Tarea>,
    idGrupo:String
):RecyclerView.Adapter<TareaAdapter.MyViewHolder>() {
    private val mContext: Context
    private val idGrupo:String
    init {
        this.idGrupo=idGrupo
        this.mContext=mContext
    }
    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val txtTituloTarea=itemView.findViewById<TextView>(R.id.txtTituloTarea)
        val txtDescTarea=itemView.findViewById<TextView>(R.id.txtDescTarea)
        val txtFechaTarea=itemView.findViewById<TextView>(R.id.txtFechaTarea)
        val btn_tareaver=itemView.findViewById<Button>(R.id.btn_send)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.tarea_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtTituloTarea.text=tareaList[position].getNombreTarea()
        holder.txtDescTarea.text=tareaList[position].getDescripcion()
        holder.txtFechaTarea.text=tareaList[position].getFechaLimite()
        holder.btn_tareaver.setOnClickListener {
            //val ventanaVerTarea:Intent=Intent()
            val VentanaTareaVer = Intent(mContext, TareaVer_main::class.java)
            VentanaTareaVer.putExtra("idTarea",tareaList[position].getUID())
            VentanaTareaVer.putExtra("idGrupo",idGrupo)
            mContext.startActivity(VentanaTareaVer)
        }
    }

    override fun getItemCount(): Int {
        return tareaList.size
        //return 10
    }
}