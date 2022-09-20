package com.example.POI.ModelClasses

class SubGrupo {
    private var uid:String=""
    private var subgrupoName:String=""
    private var timeStamp:Any?=null
    var alumnosSubGrupo:ArrayList<Users>?=null

    constructor(){
        this.alumnosSubGrupo=arrayListOf<Users>()
    }
    constructor(uid: String, subgrupoName: String, timeStamp: Any?,
                alumnosSubGrupo: ArrayList<Users>?) {
        this.uid = uid
        this.subgrupoName = subgrupoName
        this.timeStamp = timeStamp
        this.alumnosSubGrupo = alumnosSubGrupo
    }

    constructor(uid: String, subgrupoName: String, timeStamp: Any?) {
        this.uid = uid
        this.subgrupoName = subgrupoName
        this.timeStamp = timeStamp
    }

    constructor(subgrupoName: String) {
        this.subgrupoName = subgrupoName
    }


    fun getUID():String?{
        return uid
    }
    fun setUID(uid:String){
        this.uid=uid!!
    }

    fun getSubgrupoName():String?{
        return subgrupoName
    }
    fun setSubgrupoName(subgrupoName: String){
        this.subgrupoName=subgrupoName!!
    }

    fun getTimeStamp():Any?{
        return timeStamp
    }
    fun setTimeStamp(timeStamp: Any?){
        this.timeStamp=timeStamp!!
    }
}