package com.miagenda.app.di

import android.content.Context
import com.miagenda.app.data.local.AppDatabase
import com.miagenda.app.data.repository.PacienteRepository
import com.miagenda.app.data.repository.SesionRepository

class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getInstance(context)

    val pacienteRepository: PacienteRepository by lazy {
        PacienteRepository(database.pacienteDao())
    }

    val sesionRepository: SesionRepository by lazy {
        SesionRepository(database.sesionDao())
    }
}
