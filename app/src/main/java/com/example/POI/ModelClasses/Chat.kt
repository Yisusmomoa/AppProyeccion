package com.example.POI.ModelClasses

import java.sql.Timestamp

class Chat {

    private var sender:String=""
    private var message:String=""
    private var receiver:String=""
    private var isseen=false
    private var url:String=""
    private var messageId:String=""
    private var timeStamp:Any?=""
    private var encryptChat:String=""
    var tipoArchivo:String=""

    constructor() {

    }
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        url: String,
        messageId: String,
        timeStamp: Any?,
        encryptChat: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
        this.timeStamp = timeStamp
        this.encryptChat=encryptChat
    }


    fun getSender():String?{
        return sender
    }
    fun setSender(sender: String?){
        this.sender=sender!!
    }

    fun getMessage():String?{
        return message
    }
    fun setMessage(message: String?){
        this.message=message!!
    }

    fun getReceiver():String?{
        return receiver
    }
    fun setReceiver(receiver: String?){
        this.receiver=receiver!!
    }

    fun getIsseen():Boolean{ //isIsSeen
        return isseen
    }
    fun setIsseen(isseen: Boolean?){
        this.isseen=isseen!!
    }

    fun getUrl():String?{
        return url
    }
    fun setUrl(url: String?){
        this.url=url!!
    }

    fun getMessageId():String?{
        return messageId
    }
    fun setMessageId(messageId: String?){
        this.messageId=messageId!!
    }

    fun getTimeStamp():Any?{
        if (this.timeStamp!=null){
            return timeStamp!!
        }
        return ""
    }
    fun setTimeStamp(timestamp: Any){
        this.timeStamp=timestamp!!
    }
    fun getEncryptChat():String?{
        return encryptChat
    }
    fun setEncryptChat(encryptChat: String?){
        this.encryptChat=encryptChat!!
    }

}