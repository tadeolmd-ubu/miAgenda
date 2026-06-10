package com.miagenda.app.ui.screens.pacientes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import com.miagenda.app.domain.mapper.toDomain
import com.miagenda.app.domain.model.Paciente
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TodosPacientesUiState(
    val searchQuery: String = "",
    val pacientes: List<Paciente> = emptyList(),
    val isLoading: Boolean = true
)

class TodosPacientesViewModel(application: Application) : AndroidViewModel(application) {

    private val pacienteRepo = (application as AgendaApp).container.pacienteRepository

    private val _uiState = MutableStateFlow(TodosPacientesUiState())
    val uiState: StateFlow<TodosPacientesUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        cargarPacientes()
    }

    private fun cargarPacientes() {
        viewModelScope.launch {
            pacienteRepo.todosPacientes.collect { entities ->
                val pacientes = entities.map { it.toDomain() }
                _uiState.value = _uiState.value.copy(
                    pacientes = pacientes,
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getPacientesFiltrados(): List<Paciente> {
        val state = _uiState.value
        if (state.searchQuery.isBlank()) return state.pacientes
        return state.pacientes.filter {
            it.nombre.contains(state.searchQuery, ignoreCase = true)
        }
    }
}
