package com.example.POI.ModelClasses

import com.google.firebase.firestore.auth.User
import java.sql.Timestamp

class Comentario {
    private var uid:String=""
    private var textoComentario:String=""
    private var timeStamp:Any?=""
    private var url:String=""
    private var userComentario:Users?=null
    constructor(){}
    constructor(
        uid:String,
        textoComentario:String,
        url:String,
        timestamp: Any?,
        userComentario: Users?){
        this.uid=uid
        this.textoComentario=textoComentario
        this.url=url
        this.timeStamp=timeStamp
        this.userComentario=userComentario
    }

    constructor(
        uid:String,
        textoComentario:String,
        timestamp: Any?,
        userComentario: Users?){
        this.uid=uid
        this.textoComentario=textoComentario
        this.timeStamp=timestamp
        this.userComentario=userComentario
    }
    fun getUID():String?{
        return uid
    }
    fun setUID(uid:String){
        this.uid=uid!!
    }

    fun getTextoComentario():String?{
        return textoComentario
    }
    fun setTextoComentario(textoComentario:String){
        this.textoComentario=textoComentario!!
    }

    fun getTimeStamp():Any?{
        return timeStamp
    }
    fun setTimeStamp(uid:Any){
        this.timeStamp=timeStamp!!
    }

    fun getUrl():String?{
        return url
    }
    fun setUrl(url:String){
        this.url=url!!
    }

    fun getUserComentario():Users?{
        return userComentario
    }
    fun setUserComentario(userComentario:Users?){
        this.userComentario=userComentario!!
    }
}