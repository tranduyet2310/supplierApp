package com.example.suppileragrimart.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RsaKey::class], version = 1)
abstract class RsaKeyDatabase : RoomDatabase() {

    abstract fun rsaKeyDao(): RsaKeyDao

    companion object {
        @Volatile
        private var INSTANCE: RsaKeyDatabase? = null

        fun getDatabase(context: Context): RsaKeyDatabase {
            val tmpInstance = INSTANCE
            if (tmpInstance != null) {
                return tmpInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RsaKeyDatabase::class.java,
                    "rsa_database.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}