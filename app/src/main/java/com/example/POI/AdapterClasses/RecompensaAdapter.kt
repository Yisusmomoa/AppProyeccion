package com.example.POI.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.ModelClasses.Recompensa
import com.example.POI.R

class RecompensaAdapter(
    private val recompensaList: List<Recompensa>
):RecyclerView.Adapter<RecompensaAdapter.MyViewHolder>() {

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val TipoRecompensa:TextView=itemView.findViewById(R.id.TipoRecompensa)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.recompensa_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.TipoRecompensa.text=recompensaList[position].getNombreRecompensa()
    }

    override fun getItemCount(): Int {
        return recompensaList.size
        //return 10
    }

}