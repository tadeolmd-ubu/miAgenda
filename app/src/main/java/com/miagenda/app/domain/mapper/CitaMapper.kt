package com.miagenda.app.domain.mapper

import com.miagenda.app.data.local.entity.CitaEntity
import com.miagenda.app.domain.model.Cita

fun CitaEntity.toDomain(nombrePaciente: String = ""): Cita = Cita(
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

fun Cita.toEntity(): CitaEntity = CitaEntity(
    id = id,
    pacienteId = pacienteId,
    fecha = fecha,
    horaInicio = horaInicio,
    horaFin = horaFin,
    motivo = motivo,
    notas = notas,
    fechaCreacion = fechaCreacion
)
