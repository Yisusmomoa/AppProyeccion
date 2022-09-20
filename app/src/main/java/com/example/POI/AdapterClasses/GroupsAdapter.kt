package com.example.POI.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.MessageChatActivity
import com.example.POI.MessageChatActivityGroup
import com.example.POI.ModelClasses.Grupo
import com.example.POI.ModelClasses.Users
import com.example.POI.R
import com.google.firebase.database.ValueEventListener
import kotlin.math.log

class GroupsAdapter(
    mContext: Context,
    private var listaGrupos: ArrayList<Grupo>)
    :RecyclerView.Adapter<GroupsAdapter.grupoViewHolder>() {
    private val mContext: Context
    //private val mGrupos:List<Grupo>
    init {
        this.mContext=mContext
        //this.mGrupos=mGrupos
    }
    //myviewholder
    class grupoViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val groupName:TextView=itemView.findViewById(R.id.txtNombreGrupoLista)

    }

    //como se verá cada elemento, se pasa cada elemento
    //el diseño del layout(mensaje, grupo)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): grupoViewHolder {
        val miView=LayoutInflater.from(parent.context).inflate(R.layout.grupo_lista,parent,false)
        return grupoViewHolder(miView)
    }

    //que info va a mostrar en cada elemento
    //Pasar la información a la tarjeta
    override fun onBindViewHolder(holder: grupoViewHolder, position: Int) {
        //var currentitem=listaGrupos[position]
        val group:Grupo=listaGrupos[position]
        holder.groupName.text=group.getGroupName()
       /* listaGrupos.forEach {
            Log.d("Grupos", "Nombre grupo: "+it.getGroupName())
            Log.d("Grupos", "**********")
        }*/

        holder.itemView.setOnClickListener {
            val options= arrayOf<CharSequence>(
                "Send message"
            )
            val builder: AlertDialog.Builder= AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position==0){
                    val intent= Intent(mContext, MessageChatActivityGroup::class.java)
                    intent.putExtra("visit_id",group.getUID())
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }
        //de que a los textview le pasas lo que venga de firebase


    }

    //cuantos elementos se van a ver en el listview
    override fun getItemCount(): Int {
        return listaGrupos.size
    }

}