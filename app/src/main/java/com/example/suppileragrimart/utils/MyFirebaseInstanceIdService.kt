package com.example.suppileragrimart.utils

import com.example.suppileragrimart.model.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.ktx.messaging

class MyFirebaseInstanceIdService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshToken = Firebase.messaging.token.toString()

        if (firebaseUser != null) {
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.CHAT_TOKEN)
        val token = Token(refreshToken)
        ref.child(firebaseUser!!.uid).setValue(token)
    }
}