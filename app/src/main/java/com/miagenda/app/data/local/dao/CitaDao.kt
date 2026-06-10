package com.miagenda.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miagenda.app.data.local.entity.CitaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {

    @Query("SELECT * FROM citas ORDER BY fecha DESC, horaInicio ASC")
    fun getAllCitas(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE fecha = :fecha ORDER BY horaInicio ASC")
    fun getCitasByFecha(fecha: Long): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE pacienteId = :pacienteId ORDER BY fecha DESC")
    fun getCitasByPacienteId(pacienteId: Long): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun getCitaById(id: Long): CitaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCita(cita: CitaEntity): Long

    @Update
    suspend fun updateCita(cita: CitaEntity)

    @Delete
    suspend fun deleteCita(cita: CitaEntity)
}
