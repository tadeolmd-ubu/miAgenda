package com.miagenda.app.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import com.miagenda.app.data.local.entity.PacienteEntity
import com.miagenda.app.domain.mapper.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetallePacienteUiState(
    val nombre: String = "",
    val telefono: String = "",
    val edad: String = "",
    val notas: String = "",
    val nombreError: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isLoaded: Boolean = false,
    val pacienteId: Long = -1,
    val isEditing: Boolean = false
)

class DetallePacienteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AgendaApp).container.pacienteRepository

    private val _uiState = MutableStateFlow(DetallePacienteUiState())
    val uiState: StateFlow<DetallePacienteUiState> = _uiState.asStateFlow()

    fun cargarPaciente(id: Long) {
        if (id == -1L) return
        viewModelScope.launch {
            val paciente = repository.getPacientePorId(id) ?: return@launch
            _uiState.value = DetallePacienteUiState(
                nombre = paciente.nombre,
                telefono = paciente.telefono,
                edad = if (paciente.edad > 0) paciente.edad.toString() else "",
                notas = paciente.notas,
                isLoaded = true,
                pacienteId = paciente.id,
                isEditing = true
            )
        }
    }

    fun onNombreChange(valor: String) {
        _uiState.value = _uiState.value.copy(nombre = valor, nombreError = false)
    }

    fun onTelefonoChange(valor: String) {
        _uiState.value = _uiState.value.copy(telefono = valor)
    }

    fun onEdadChange(valor: String) {
        val filtered = valor.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(edad = filtered)
    }

    fun onNotasChange(valor: String) {
        _uiState.value = _uiState.value.copy(notas = valor)
    }

    fun guardarPaciente(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.nombre.isBlank()) {
            _uiState.value = state.copy(nombreError = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)

            val entity = PacienteEntity(
                id = if (state.pacienteId > 0) state.pacienteId else 0,
                nombre = state.nombre.trim(),
                telefono = state.telefono.trim(),
                edad = state.edad.toIntOrNull() ?: 0,
                notas = state.notas.trim()
            )

            if (state.pacienteId > 0) {
                repository.actualizarPaciente(entity)
            } else {
                repository.guardarPaciente(entity)
            }

            onSuccess()
        }
    }

    fun eliminarPaciente(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true)
            val entity = PacienteEntity(
                id = _uiState.value.pacienteId,
                nombre = _uiState.value.nombre,
                telefono = _uiState.value.telefono,
                edad = _uiState.value.edad.toIntOrNull() ?: 0,
                notas = _uiState.value.notas
            )
            repository.eliminarPaciente(entity)
            onDeleted()
        }
    }
}
