package com.miagenda.app.data.repository

import com.miagenda.app.data.local.dao.SesionDao
import com.miagenda.app.data.local.entity.SesionEntity
import kotlinx.coroutines.flow.Flow

class SesionRepository(
    private val sesionDao: SesionDao
) {

    val todasLasSesiones: Flow<List<SesionEntity>> = sesionDao.getAllSesiones()

    fun getSesionesPorFecha(fecha: Long): Flow<List<SesionEntity>> {
        return sesionDao.getSesionesByFecha(fecha)
    }

    fun getSesionesPorPaciente(pacienteId: Long): Flow<List<SesionEntity>> {
        return sesionDao.getSesionesByPacienteId(pacienteId)
    }

    suspend fun getSesionPorId(id: Long): SesionEntity? {
        return sesionDao.getSesionById(id)
    }

    suspend fun guardarSesion(sesion: SesionEntity): Long {
        return sesionDao.insertSesion(sesion)
    }

    suspend fun actualizarSesion(sesion: SesionEntity) {
        sesionDao.updateSesion(sesion)
    }

    suspend fun eliminarSesion(sesion: SesionEntity) {
        sesionDao.deleteSesion(sesion)
    }
}
