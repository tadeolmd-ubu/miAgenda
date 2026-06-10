package com.miagenda.app.ui.screens.pacientes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import com.miagenda.app.domain.mapper.toDomain
import com.miagenda.app.domain.model.Paciente
import com.miagenda.app.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistorialPacienteUiState(
    val paciente: Paciente? = null,
    val sesiones: List<Sesion> = emptyList(),
    val isLoading: Boolean = true
)

class HistorialPacienteViewModel(application: Application) : AndroidViewModel(application) {

    private val pacienteRepo = (application as AgendaApp).container.pacienteRepository
    private val sesionRepo = (application as AgendaApp).container.sesionRepository

    private val _uiState = MutableStateFlow(HistorialPacienteUiState())
    val uiState: StateFlow<HistorialPacienteUiState> = _uiState.asStateFlow()

    fun cargarHistorial(pacienteId: Long) {
        if (pacienteId == -1L) return
        viewModelScope.launch {
            val pacienteEntity = pacienteRepo.getPacientePorId(pacienteId) ?: return@launch
            val paciente = pacienteEntity.toDomain()

            sesionRepo.getSesionesPorPaciente(pacienteId).collect { sesionEntities ->
                val sesiones = sesionEntities.map { entity ->
                    entity.toDomain(nombrePaciente = paciente.nombre)
                }
                _uiState.value = HistorialPacienteUiState(
                    paciente = paciente,
                    sesiones = sesiones,
                    isLoading = false
                )
            }
        }
    }
}
