package com.example.POI.ModelClasses

class Publicacion {
    private var uid:String=""
    private var titulo:String=""
    private var cuerpo:String=""
    private var url:String=""
    private var timeStamp:Any?=""
    private var userPublicacion:Users?=null
    //lateinit var publicaciones:Map<String,Publicacion>
    lateinit var comentarios:ArrayList<Comentario>
    constructor(){
        this.comentarios=arrayListOf<Comentario>()
    }
    constructor(
        uid: String,
        titulo: String,
        cuerpo: String,
        url: String,
        timeStamp: Any?,
        userPublicacion: Users?
    ) {
        this.uid = uid
        this.titulo = titulo
        this.cuerpo = cuerpo
        this.url = url
        this.timeStamp = timeStamp
        this.userPublicacion = userPublicacion
        this.comentarios=arrayListOf<Comentario>()
    }



    constructor(
        uid: String,
        titulo: String,
        cuerpo: String,
        timeStamp: Any?,
        userPublicacion: Users?
    ) {
        this.uid = uid
        this.titulo = titulo
        this.cuerpo = cuerpo
        this.timeStamp = timeStamp
        this.userPublicacion = userPublicacion
        this.comentarios=arrayListOf<Comentario>()
    }

    constructor(
        uid: String,
        titulo: String,
        cuerpo: String,
        url: String,
        timeStamp: Any?,
        userPublicacion: Users?,
        comentarios:ArrayList<Comentario>
    ) {
        this.uid = uid
        this.titulo = titulo
        this.cuerpo = cuerpo
        this.url = url
        this.timeStamp = timeStamp
        this.userPublicacion = userPublicacion
        this.comentarios=comentarios
    }

    fun getUID():String?{
        return uid
    }
    fun setUID(uid:String){
        this.uid=uid!!
    }

    fun getTitulo():String?{
        return titulo
    }
    fun setTitulo(titulo:String){
        this.titulo=titulo!!
    }

    fun getCuerpo():String?{
        return cuerpo
    }
    fun setCuerpo(cuerpo:String){
        this.cuerpo=cuerpo!!
    }

    fun getUrl():String?{
        return url
    }
    fun setUrl(url:String){
        this.url=url!!
    }

    fun getTimeStamp():Any?{
        return timeStamp
    }
    fun setTimeStamp(timeStamp:Any){
        this.timeStamp=timeStamp!!
    }

    fun getUserPublicacion():Users?{
        return userPublicacion
    }
    fun setUserPublicacion(userPublicacion:Users?){
        this.userPublicacion=userPublicacion!!
    }

}