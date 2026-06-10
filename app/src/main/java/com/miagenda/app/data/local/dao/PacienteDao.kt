package com.miagenda.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miagenda.app.data.local.entity.PacienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes ORDER BY nombre ASC")
    fun getAllPacientes(): Flow<List<PacienteEntity>>

    @Query("SELECT * FROM pacientes WHERE id = :id")
    suspend fun getPacienteById(id: Long): PacienteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaciente(paciente: PacienteEntity): Long

    @Update
    suspend fun updatePaciente(paciente: PacienteEntity)

    @Delete
    suspend fun deletePaciente(paciente: PacienteEntity)

    @Query("DELETE FROM pacientes")
    suspend fun deleteAll()
}
