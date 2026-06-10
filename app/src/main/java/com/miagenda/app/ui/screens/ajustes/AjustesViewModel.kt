package com.miagenda.app.ui.screens.ajustes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.miagenda.app.AgendaApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AjustesUiState(
    val isDeleting: Boolean = false,
    val deleteDone: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class AjustesViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as AgendaApp).container

    private val _uiState = MutableStateFlow(AjustesUiState())
    val uiState: StateFlow<AjustesUiState> = _uiState.asStateFlow()

    fun mostrarDialogoBorrar() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun ocultarDialogoBorrar() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun borrarTodosLosDatos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, showDeleteDialog = false)
            container.borrarTodosLosDatos()
            _uiState.value = _uiState.value.copy(isDeleting = false, deleteDone = true)
        }
    }

    fun resetearEstado() {
        _uiState.value = AjustesUiState()
    }
}
