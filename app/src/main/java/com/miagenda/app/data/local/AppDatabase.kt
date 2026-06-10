package com.miagenda.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.miagenda.app.data.local.dao.CitaDao
import com.miagenda.app.data.local.dao.PacienteDao
import com.miagenda.app.data.local.entity.CitaEntity
import com.miagenda.app.data.local.entity.PacienteEntity

@Database(
    entities = [PacienteEntity::class, CitaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pacienteDao(): PacienteDao
    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miagenda.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
