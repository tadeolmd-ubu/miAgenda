package com.miagenda.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "citas",
    foreignKeys = [
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["pacienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("pacienteId"),
        Index("fecha")
    ]
)
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pacienteId: Long,
    val fecha: Long,
    val horaInicio: String,
    val horaFin: String,
    val motivo: String,
    val notas: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)
