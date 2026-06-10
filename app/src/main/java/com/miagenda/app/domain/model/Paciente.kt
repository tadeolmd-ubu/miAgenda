package com.miagenda.app.domain.model

data class Paciente(
    val id: Long = 0,
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
    val notas: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)
