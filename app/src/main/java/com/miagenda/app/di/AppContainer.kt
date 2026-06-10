package com.miagenda.app.di

import android.content.Context
import com.miagenda.app.data.local.AppDatabase
import com.miagenda.app.data.repository.CitaRepository
import com.miagenda.app.data.repository.PacienteRepository

class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getInstance(context)

    val pacienteRepository: PacienteRepository by lazy {
        PacienteRepository(database.pacienteDao())
    }

    val citaRepository: CitaRepository by lazy {
        CitaRepository(database.citaDao())
    }
}
