package com.miagenda.app.ui.screens.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import com.miagenda.app.data.local.entity.PacienteEntity
import com.miagenda.app.domain.mapper.toDomain
import com.miagenda.app.domain.model.Paciente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ListaPacientesUiState(
    val pacientes: List<Paciente> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class ListaPacientesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AgendaApp).container.pacienteRepository

    private val _uiState = MutableStateFlow(ListaPacientesUiState())
    val uiState: StateFlow<ListaPacientesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(
                repository.todosPacientes,
                _searchQuery
            ) { pacientes, query ->
                val filtrados = if (query.isBlank()) {
                    pacientes
                } else {
                    pacientes.filter {
                        it.nombre.contains(query, ignoreCase = true) ||
                        it.telefono.contains(query, ignoreCase = true)
                    }
                }
                ListaPacientesUiState(
                    pacientes = filtrados.map { it.toDomain() },
                    searchQuery = query,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun eliminarPaciente(paciente: PacienteEntity) {
        viewModelScope.launch {
            repository.eliminarPaciente(paciente)
        }
    }
}
