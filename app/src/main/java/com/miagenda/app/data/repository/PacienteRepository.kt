package com.miagenda.app.data.repository

import com.miagenda.app.data.local.dao.PacienteDao
import com.miagenda.app.data.local.entity.PacienteEntity
import kotlinx.coroutines.flow.Flow

class PacienteRepository(
    private val pacienteDao: PacienteDao
) {

    val todosPacientes: Flow<List<PacienteEntity>> = pacienteDao.getAllPacientes()

    suspend fun getPacientePorId(id: Long): PacienteEntity? {
        return pacienteDao.getPacienteById(id)
    }

    suspend fun guardarPaciente(paciente: PacienteEntity): Long {
        return pacienteDao.insertPaciente(paciente)
    }

    suspend fun actualizarPaciente(paciente: PacienteEntity) {
        pacienteDao.updatePaciente(paciente)
    }

    suspend fun eliminarPaciente(paciente: PacienteEntity) {
        pacienteDao.deletePaciente(paciente)
    }
}
