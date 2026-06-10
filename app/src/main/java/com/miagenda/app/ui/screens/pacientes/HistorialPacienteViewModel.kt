package com.miagenda.app.ui.screens.pacientes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import com.miagenda.app.data.local.entity.SesionEntity
import com.miagenda.app.domain.mapper.toDomain
import com.miagenda.app.domain.mapper.toEntity
import com.miagenda.app.domain.model.Paciente
import com.miagenda.app.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HistorialPacienteUiState(
    val paciente: Paciente? = null,
    val sesiones: List<Sesion> = emptyList(),
    val isLoading: Boolean = true
)

data class HistorialDialogState(
    val show: Boolean = false,
    val editSesionId: Long? = null,
    val fecha: LocalDate = LocalDate.now(),
    val selectedPacienteId: Long? = null,
    val horaInicio: String = "",
    val horaFin: String = "",
    val motivo: String = "",
    val error: String? = null,
    val isSaving: Boolean = false
) {
    val isEditing: Boolean get() = editSesionId != null
}

class HistorialPacienteViewModel(application: Application) : AndroidViewModel(application) {

    private val pacienteRepo = (application as AgendaApp).container.pacienteRepository
    private val sesionRepo = (application as AgendaApp).container.sesionRepository

    private val _uiState = MutableStateFlow(HistorialPacienteUiState())
    val uiState: StateFlow<HistorialPacienteUiState> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(HistorialDialogState())
    val dialogState: StateFlow<HistorialDialogState> = _dialogState.asStateFlow()

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

    fun abrirEditarSesion(sesion: Sesion) {
        _dialogState.value = HistorialDialogState(
            show = true,
            editSesionId = sesion.id,
            fecha = LocalDate.ofEpochDay(sesion.fecha),
            selectedPacienteId = sesion.pacienteId,
            horaInicio = sesion.horaInicio,
            horaFin = sesion.horaFin,
            motivo = sesion.motivo
        )
    }

    fun cerrarDialogo() {
        _dialogState.value = HistorialDialogState(show = false)
    }

    fun onHoraInicioChange(hora: String) {
        _dialogState.value = _dialogState.value.copy(horaInicio = hora, error = null)
    }

    fun onHoraFinChange(hora: String) {
        _dialogState.value = _dialogState.value.copy(horaFin = hora, error = null)
    }

    fun onMotivoChange(motivo: String) {
        _dialogState.value = _dialogState.value.copy(motivo = motivo)
    }

    fun guardarSesion() {
        val state = _dialogState.value
        if (state.horaInicio.isBlank()) {
            _dialogState.value = state.copy(error = "Ingresa la hora de inicio")
            return
        }

        viewModelScope.launch {
            _dialogState.value = state.copy(isSaving = true)

            val fechaEpoch = state.fecha.toEpochDay()

            val entity = SesionEntity(
                id = state.editSesionId ?: 0,
                pacienteId = _uiState.value.paciente!!.id,
                fecha = fechaEpoch,
                horaInicio = state.horaInicio,
                horaFin = state.horaFin,
                motivo = state.motivo,
                notas = ""
            )

            sesionRepo.actualizarSesion(entity)
            cerrarDialogo()
        }
    }

    fun eliminarSesion(sesion: Sesion) {
        viewModelScope.launch {
            sesionRepo.eliminarSesion(sesion.toEntity())
        }
    }
}
