package com.miagenda.app.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miagenda.app.domain.model.Paciente
import com.miagenda.app.domain.model.Sesion
import com.miagenda.app.ui.components.EmptyState
import com.miagenda.app.ui.components.MonthlyCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPacientesScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateToNuevo: () -> Unit,
    onNavigateToPacientes: () -> Unit,
    viewModel: ListaPacientesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()

    if (dialogState.show) {
        CrearSesionDialog(
            dialogState = dialogState,
            pacientes = uiState.pacientes,
            onPacienteSelected = viewModel::onPacienteSeleccionado,
            onHoraInicioChange = viewModel::onHoraInicioChange,
            onHoraFinChange = viewModel::onHoraFinChange,
            onMotivoChange = viewModel::onMotivoChange,
            onGuardar = viewModel::guardarSesion,
            onDismiss = viewModel::cerrarDialogo
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Agenda") },
                actions = {
                    IconButton(onClick = onNavigateToPacientes) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Pacientes"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNuevo,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo paciente"
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MonthlyCalendar(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = viewModel::onDateSelected,
                    onMonthChange = viewModel::onMonthChange,
                    datesWithAppointments = uiState.fechasConSesiones,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                DayAppointmentsSection(
                    selectedDate = uiState.selectedDate,
                    sesiones = uiState.sesionesDelDia,
                    onDeleteSesion = viewModel::eliminarSesion,
                    onAddSesion = viewModel::abrirCrearSesion
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrearSesionDialog(
    dialogState: CrearSesionDialogState,
    pacientes: List<Paciente>,
    onPacienteSelected: (Long) -> Unit,
    onHoraInicioChange: (String) -> Unit,
    onHoraFinChange: (String) -> Unit,
    onMotivoChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))
    val dateText = dialogState.fecha.format(dateFormatter).replaceFirstChar { it.uppercase() }
    var expanded by remember { mutableStateOf(false) }
    val selectedPaciente = pacientes.find { it.id == dialogState.selectedPacienteId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Nueva sesión")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedPaciente?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Paciente") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        pacientes.forEach { paciente ->
                            DropdownMenuItem(
                                text = { Text(paciente.nombre) },
                                onClick = {
                                    onPacienteSelected(paciente.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dialogState.horaInicio,
                    onValueChange = onHoraInicioChange,
                    label = { Text("Hora inicio") },
                    placeholder = { Text("HH:mm") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dialogState.horaFin,
                    onValueChange = onHoraFinChange,
                    label = { Text("Hora fin (opcional)") },
                    placeholder = { Text("HH:mm") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dialogState.motivo,
                    onValueChange = onMotivoChange,
                    label = { Text("Motivo (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                if (dialogState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dialogState.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onGuardar,
                enabled = !dialogState.isSaving
            ) {
                if (dialogState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DayAppointmentsSection(
    selectedDate: LocalDate,
    sesiones: List<Sesion>,
    onDeleteSesion: (Sesion) -> Unit,
    onAddSesion: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))
        val dateText = selectedDate.format(formatter)
            .replaceFirstChar { it.uppercase() }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onAddSesion(selectedDate) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva sesión",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (sesiones.isEmpty()) {
            EmptyState(
                mensaje = "No hay sesiones para este día",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(sesiones, key = { it.id }) { sesion ->
                    SesionCard(
                        sesion = sesion,
                        onDelete = { onDeleteSesion(sesion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SesionCard(
    sesion: Sesion,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = sesion.horaInicio,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (sesion.horaFin.isNotBlank()) {
                        Text(
                            text = sesion.horaFin,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sesion.nombrePaciente,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (sesion.motivo.isNotBlank()) {
                    Text(
                        text = sesion.motivo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar sesión",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
