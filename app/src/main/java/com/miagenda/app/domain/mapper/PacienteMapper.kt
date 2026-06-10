package com.miagenda.app.domain.mapper

import com.miagenda.app.data.local.entity.PacienteEntity
import com.miagenda.app.domain.model.Paciente

fun PacienteEntity.toDomain(): Paciente = Paciente(
    id = id,
    nombre = nombre,
    telefono = telefono,
    edad = edad,
    notas = notas,
    fechaCreacion = fechaCreacion
)

fun Paciente.toEntity(): PacienteEntity = PacienteEntity(
    id = id,
    nombre = nombre,
    telefono = telefono,
    edad = edad,
    notas = notas,
    fechaCreacion = fechaCreacion
)
