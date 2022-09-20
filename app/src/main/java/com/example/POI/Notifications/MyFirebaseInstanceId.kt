package com.example.POI.Notifications

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId:FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val firebaseUser= FirebaseAuth.getInstance().currentUser
        val refreshToken=FirebaseMessaging.getInstance().token
        if (firebaseUser!=null){
            updateToken(refreshToken)
        }


    }

    private fun updateToken(refreshToken: Task<String>) {
        val firebaseUser= FirebaseAuth.getInstance().currentUser
        val ref=FirebaseDatabase.getInstance().getReference().child("Tokens")
        val token=Token(refreshToken!!.toString())
        ref.child(firebaseUser!!.uid).setValue(token)
    }

}