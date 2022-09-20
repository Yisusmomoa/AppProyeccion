package com.example.POI.ModelClasses

class RecompensaTUPTM {

    private var nombreRecompensa:String=""
    lateinit var alumnosRecompensa:ArrayList<Users>
    constructor(){
        this.alumnosRecompensa=arrayListOf<Users>()
    }
    constructor(nombreRecompensa: String,
                alumnosRecompensa: ArrayList<Users>)
    {

        this.nombreRecompensa=nombreRecompensa
        this.alumnosRecompensa=alumnosRecompensa
    }
    fun getNombreRecompensa():String{
        return  nombreRecompensa
    }
    fun setNombreRecompensa(nombreRecompensa:String){
        this.nombreRecompensa=nombreRecompensa
    }



}