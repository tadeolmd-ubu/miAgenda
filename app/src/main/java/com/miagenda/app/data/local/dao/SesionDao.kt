package com.miagenda.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miagenda.app.data.local.entity.SesionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionDao {

    @Query("SELECT * FROM sesiones ORDER BY fecha DESC, horaInicio ASC")
    fun getAllSesiones(): Flow<List<SesionEntity>>

    @Query("SELECT * FROM sesiones WHERE fecha = :fecha ORDER BY horaInicio ASC")
    fun getSesionesByFecha(fecha: Long): Flow<List<SesionEntity>>

    @Query("SELECT * FROM sesiones WHERE pacienteId = :pacienteId ORDER BY fecha DESC")
    fun getSesionesByPacienteId(pacienteId: Long): Flow<List<SesionEntity>>

    @Query("SELECT * FROM sesiones WHERE id = :id")
    suspend fun getSesionById(id: Long): SesionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSesion(sesion: SesionEntity): Long

    @Update
    suspend fun updateSesion(sesion: SesionEntity)

    @Delete
    suspend fun deleteSesion(sesion: SesionEntity)
}
