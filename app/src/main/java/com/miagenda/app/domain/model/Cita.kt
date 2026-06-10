package com.miagenda.app.domain.model

data class Cita(
    val id: Long = 0,
    val pacienteId: Long,
    val fecha: Long,
    val horaInicio: String,
    val horaFin: String,
    val motivo: String,
    val notas: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val nombrePaciente: String = ""
)
