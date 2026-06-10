package com.miagenda.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pacientes")
data class PacienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val telefono: String,
    val edad: Int = 0,
    val notas: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)
