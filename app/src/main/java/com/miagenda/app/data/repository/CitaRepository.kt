package com.miagenda.app.data.repository

import com.miagenda.app.data.local.dao.CitaDao
import com.miagenda.app.data.local.entity.CitaEntity
import kotlinx.coroutines.flow.Flow

class CitaRepository(
    private val citaDao: CitaDao
) {

    val todasLasCitas: Flow<List<CitaEntity>> = citaDao.getAllCitas()

    fun getCitasPorFecha(fecha: Long): Flow<List<CitaEntity>> {
        return citaDao.getCitasByFecha(fecha)
    }

    fun getCitasPorPaciente(pacienteId: Long): Flow<List<CitaEntity>> {
        return citaDao.getCitasByPacienteId(pacienteId)
    }

    suspend fun getCitaPorId(id: Long): CitaEntity? {
        return citaDao.getCitaById(id)
    }

    suspend fun guardarCita(cita: CitaEntity): Long {
        return citaDao.insertCita(cita)
    }

    suspend fun actualizarCita(cita: CitaEntity) {
        citaDao.updateCita(cita)
    }

    suspend fun eliminarCita(cita: CitaEntity) {
        citaDao.deleteCita(cita)
    }
}
