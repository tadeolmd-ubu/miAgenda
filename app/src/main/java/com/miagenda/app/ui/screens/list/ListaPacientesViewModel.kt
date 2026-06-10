package com.miagenda.app.ui.screens.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import com.miagenda.app.data.local.entity.PacienteEntity
import com.miagenda.app.domain.mapper.toDomain
import com.miagenda.app.domain.mapper.toEntity
import com.miagenda.app.domain.model.Paciente
import com.miagenda.app.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class ListaPacientesUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val sesionesDelDia: List<Sesion> = emptyList(),
    val fechasConSesiones: Set<LocalDate> = emptySet(),
    val pacientes: List<Paciente> = emptyList(),
    val isLoading: Boolean = true
)

class ListaPacientesViewModel(application: Application) : AndroidViewModel(application) {

    private val pacienteRepo = (application as AgendaApp).container.pacienteRepository
    private val sesionRepo = (application as AgendaApp).container.sesionRepository

    private val _uiState = MutableStateFlow(ListaPacientesUiState())
    val uiState: StateFlow<ListaPacientesUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    init {
        observeSesiones()
    }

    private fun observeSesiones() {
        viewModelScope.launch {
            combine(
                sesionRepo.todasLasSesiones,
                _selectedDate,
                pacienteRepo.todosPacientes
            ) { sesiones, selectedDate, pacientes ->
                val pacienteMap = pacientes.associateBy { it.id }

                val fechasConSesiones = sesiones.map {
                    LocalDate.ofEpochDay(it.fecha)
                }.toSet()

                val sesionesDelDia = sesiones
                    .filter { it.fecha == selectedDate.toEpochDay() }
                    .map { sesionEntity ->
                        val paciente = pacienteMap[sesionEntity.pacienteId]
                        sesionEntity.toDomain(nombrePaciente = paciente?.nombre ?: "Desconocido")
                    }
                    .sortedBy { it.horaInicio }

                val pacientesDomain = pacientes.map { it.toDomain() }

                ListaPacientesUiState(
                    selectedDate = selectedDate,
                    sesionesDelDia = sesionesDelDia,
                    fechasConSesiones = fechasConSesiones,
                    pacientes = pacientesDomain,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onMonthChange(yearMonth: YearMonth) {
        val currentDate = _selectedDate.value
        val newDate = if (yearMonth.lengthOfMonth() >= currentDate.dayOfMonth) {
            yearMonth.atDay(currentDate.dayOfMonth.coerceAtMost(yearMonth.lengthOfMonth()))
        } else {
            yearMonth.atEndOfMonth()
        }
        _selectedDate.value = newDate
    }

    fun eliminarSesion(sesion: Sesion) {
        viewModelScope.launch {
            sesionRepo.eliminarSesion(sesion.toEntity())
        }
    }

    fun eliminarPaciente(pacienteEntity: PacienteEntity) {
        viewModelScope.launch {
            pacienteRepo.eliminarPaciente(pacienteEntity)
        }
    }
}
