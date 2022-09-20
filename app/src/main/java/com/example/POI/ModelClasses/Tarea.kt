package com.example.POI.ModelClasses

import java.sql.Timestamp

class Tarea {
    private var uid:String=""
    private var descripcion:String=""
    private var nombreTarea:String=""
    private var timestamp:Any?=""
    private var fechaLimite:String=""
    private var url:String=""
    private var userTarea:Users?=null
    private var status:Boolean?=false
    constructor(
    )

    constructor(uid:String,nombreTarea:String,
                descripcion:String,timestamp: Any?,
                fechaLimite:String,url:String?,
                userTarea:Users?
    ){
        this.uid=uid
        this.nombreTarea=nombreTarea
        this.descripcion=descripcion
        this.timestamp=timestamp
        this.fechaLimite=fechaLimite
        this.url= url!!
        this.userTarea=userTarea
    }

    constructor(uid:String,nombreTarea:String,
                descripcion:String, timestamp: Any?,
                fechaLimite:String, userTarea:Users?){
        this.uid=uid
        this.nombreTarea=nombreTarea
        this.descripcion=descripcion
        this.timestamp=timestamp
        this.fechaLimite=fechaLimite
        this.userTarea=userTarea
    }

    fun getUID():String?{
        return  uid
    }
    fun setUID(uid: String){
        this.uid=uid
    }

    fun getNombreTarea():String?{
        return nombreTarea
    }
    fun setNombreTarea(nombreTarea: String){
        this.nombreTarea=nombreTarea
    }

    fun getDescripcion():String?{
        return descripcion
    }
    fun setDescripcion(descripcion: String){
        this.descripcion=descripcion
    }

    fun getFechaLimite():String?{
        return fechaLimite
    }
    fun setFechaLimite(fechaLimite: String){
        this.fechaLimite=fechaLimite
    }

    fun getTimeStamp():Any?{
        return timestamp
    }
    fun setTimeStamp(timeStamp:Any){
        this.timestamp=timeStamp!!
    }

    fun getUrl():String?{
        return url
    }
    fun setUrl(url:String){
        this.url=url!!
    }

    fun getUserTarea():Users?{
        return userTarea
    }
    fun setUserTarea(userTarea:Users?){
        this.userTarea=userTarea!!
    }

    fun getStatus():Boolean{
        return status!!
    }
    fun setStatus(status:Boolean){
        this.status=status
    }

}