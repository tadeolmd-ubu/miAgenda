package com.miagenda.app.ui.screens.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import com.miagenda.app.data.local.entity.PacienteEntity
import com.miagenda.app.domain.mapper.toDomain
import com.miagenda.app.domain.mapper.toEntity
import com.miagenda.app.domain.model.Cita
import com.miagenda.app.domain.model.Paciente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class ListaPacientesUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val citasDelDia: List<Cita> = emptyList(),
    val fechasConCitas: Set<LocalDate> = emptySet(),
    val pacientes: List<Paciente> = emptyList(),
    val isLoading: Boolean = true
)

class ListaPacientesViewModel(application: Application) : AndroidViewModel(application) {

    private val pacienteRepo = (application as AgendaApp).container.pacienteRepository
    private val citaRepo = (application as AgendaApp).container.citaRepository

    private val _uiState = MutableStateFlow(ListaPacientesUiState())
    val uiState: StateFlow<ListaPacientesUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    init {
        observeCitas()
    }

    private fun observeCitas() {
        viewModelScope.launch {
            combine(
                citaRepo.todasLasCitas,
                _selectedDate,
                pacienteRepo.todosPacientes
            ) { citas, selectedDate, pacientes ->
                val pacienteMap = pacientes.associateBy { it.id }

                val fechasConCitas = citas.map {
                    LocalDate.ofEpochDay(it.fecha)
                }.toSet()

                val citasDelDia = citas
                    .filter { it.fecha == selectedDate.toEpochDay() }
                    .map { citaEntity ->
                        val paciente = pacienteMap[citaEntity.pacienteId]
                        citaEntity.toDomain(nombrePaciente = paciente?.nombre ?: "Desconocido")
                    }
                    .sortedBy { it.horaInicio }

                val pacientesDomain = pacientes.map { it.toDomain() }

                ListaPacientesUiState(
                    selectedDate = selectedDate,
                    citasDelDia = citasDelDia,
                    fechasConCitas = fechasConCitas,
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

    fun eliminarCita(cita: Cita) {
        viewModelScope.launch {
            citaRepo.eliminarCita(cita.toEntity())
        }
    }

    fun eliminarPaciente(pacienteEntity: PacienteEntity) {
        viewModelScope.launch {
            pacienteRepo.eliminarPaciente(pacienteEntity)
        }
    }
}
