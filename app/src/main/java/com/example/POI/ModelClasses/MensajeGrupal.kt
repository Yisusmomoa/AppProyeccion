package com.example.POI.ModelClasses

import com.google.firebase.database.Exclude

class MensajeGrupal(var id: String = "",
                    var contenido: String = "",
                    var de: String = "",
                    var timeStamp: Any? = null,
                    val grupoId:String="",
                    //var tipoArchivo:String="",
                    var url:String=""
) {
    @Exclude
    var esMio: Boolean = false
}
