package com.example.suppileragrimart.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_rsa")
data class RsaKey (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val email: String,
    val publicKey: String,
    val privateKey: String
)