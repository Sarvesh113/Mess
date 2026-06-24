package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VendorEntity::class, OrderEntity::class], version = 1, exportSchema = false)
abstract class MessMateDatabase : RoomDatabase() {
    abstract fun dao(): MessMateDao

    companion object {
        @Volatile
        private var INSTANCE: MessMateDatabase? = null

        fun getDatabase(context: Context): MessMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessMateDatabase::class.java,
                    "messmate_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
