package com.example.POI.ModelClasses

import com.google.firebase.firestore.auth.User

class Grupo {
    private var uid:String=""
    private var grupoName:String=""
    private var timeStamp:Any?=null
    /*var subGrupos:List<SubGrupo>?=null
    var alumnosGrupo:List<Users>?=null*/

    lateinit var subGrupos:Map<String,SubGrupo>
    lateinit var alumnosGrupo:Map<String,Users>


    constructor(){}
    constructor(uisd: String, grupoName: String, timeStamp: Any?) {
        this.uid = uid
        this.grupoName = grupoName
        this.timeStamp = timeStamp
    }


    constructor(
        uid: String,
        grupoName: String,
        timeStamp: Any?,
        subGrupos: Map<String,SubGrupo>?,
        alumnosGrupo: Map<String,Users>?
    ) {
        this.uid = uid
        this.grupoName = grupoName
        this.timeStamp = timeStamp
        this.subGrupos = subGrupos!!
        this.alumnosGrupo = alumnosGrupo!!
    }

    constructor(uid: String, grupoName: String) {
        this.uid = uid
        this.grupoName = grupoName
    }

    constructor(uid: String, grupoName: String, alumnosGrupo: Map<String,Users>?) {
        this.uid = uid
        this.grupoName = grupoName
        this.alumnosGrupo = alumnosGrupo!!
    }


    fun getUID():String?{
        return uid
    }
    fun setUID(uid:String){
        this.uid=uid!!
    }

    fun getGroupName():String?{
        return grupoName
    }
    fun setGroupName(groupName:String){
        this.grupoName=groupName!!
    }

    override fun toString(): String {
        return grupoName
    }


}