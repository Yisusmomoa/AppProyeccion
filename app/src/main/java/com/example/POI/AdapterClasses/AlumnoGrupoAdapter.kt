package com.example.POI.AdapterClasses

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.POI.Interface.ClickInterface
import com.example.POI.ModelClasses.Users
import com.example.POI.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class AlumnoGrupoAdapter(
    private val userList:ArrayList<Users>
):RecyclerView.Adapter<AlumnoGrupoAdapter.viewHolderAlumnoGrupo>(){

    var listaAlumnosNuevoGrupo= arrayListOf<Users>()



     class viewHolderAlumnoGrupo(itemView:View): RecyclerView.ViewHolder(itemView) {
        val checkboxAlumno=itemView.findViewById<CheckBox>(R.id.checkboxAlumno)
        val txtUsernameGrupo:TextView=itemView.findViewById(R.id.txtUsernameGrupo)
        val ProfileImgGrupo:ImageView=itemView.findViewById(R.id.ProfileImgGrupo)


    }

    //que se va a mostrar, que layout
    //la plantilla, card
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderAlumnoGrupo {
        listaAlumnosNuevoGrupo.clear()
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.alumnoitem,parent,false)
        return  viewHolderAlumnoGrupo(itemView)
    }

    //que va a llevar cada item de ese layout
    override fun onBindViewHolder(holder: viewHolderAlumnoGrupo, position: Int) {
            val alumnoAux=userList[position]
            holder.txtUsernameGrupo.text=alumnoAux.getUsername()
            Picasso.get().load(alumnoAux.getProfile()).into(holder.ProfileImgGrupo)

            holder.checkboxAlumno.setOnClickListener {
                if (!holder.checkboxAlumno.isChecked){
                    listaAlumnosNuevoGrupo.remove(userList[position])
                }
                else{
                    listaAlumnosNuevoGrupo.add(userList[position])
                }

                Log.d("Inscribir", "Lista Alumnos: "+listaAlumnosNuevoGrupo)
            }
    }
    fun regresarAlumnosSelecc():List<Users>{
        return listaAlumnosNuevoGrupo
    }

    //cuantos items van a estar dentro del recyclery view
    override fun getItemCount(): Int {
        return userList.size
    }



}