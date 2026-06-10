package com.miagenda.app.domain.mapper

import com.miagenda.app.data.local.entity.SesionEntity
import com.miagenda.app.domain.model.Sesion

fun SesionEntity.toDomain(nombrePaciente: String = ""): Sesion = Sesion(
    id = id,
    pacienteId = pacienteId,
    fecha = fecha,
    horaInicio = horaInicio,
    horaFin = horaFin,
    motivo = motivo,
    notas = notas,
    fechaCreacion = fechaCreacion,
    nombrePaciente = nombrePaciente
)

fun Sesion.toEntity(): SesionEntity = SesionEntity(
    id = id,
    pacienteId = pacienteId,
    fecha = fecha,
    horaInicio = horaInicio,
    horaFin = horaFin,
    motivo = motivo,
    notas = notas,
    fechaCreacion = fechaCreacion
)
