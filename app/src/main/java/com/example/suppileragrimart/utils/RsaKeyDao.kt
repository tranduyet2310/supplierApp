package com.example.suppileragrimart.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.suppileragrimart.model.RsaKey

@Dao
interface RsaKeyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRsaKey(rsaKey: RsaKey)
    @Query("SELECT * FROM tbl_rsa WHERE email = :clientEmail")
    fun getRsaKey(clientEmail: String): RsaKey?
}