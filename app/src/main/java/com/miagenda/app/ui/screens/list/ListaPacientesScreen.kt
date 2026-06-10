package com.miagenda.app.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miagenda.app.domain.model.Paciente
import com.miagenda.app.domain.model.Sesion
import com.miagenda.app.ui.components.CalendarGridInfo
import com.miagenda.app.ui.components.EmptyState
import com.miagenda.app.ui.components.MonthlyCalendar
import kotlin.math.roundToInt
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class DragState(
    val paciente: Paciente,
    val position: Offset
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPacientesScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateToNuevo: () -> Unit,
    onNavigateToPacientes: () -> Unit,
    onNavigateToAjustes: () -> Unit,
    viewModel: ListaPacientesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()

    var dragState by remember { mutableStateOf<DragState?>(null) }
    var calendarGridPosition by remember { mutableStateOf<CalendarGridInfo?>(null) }

    if (dialogState.show) {
        SesionDialog(
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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mi Agenda") },
                    actions = {
                        IconButton(onClick = onNavigateToAjustes) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ajustes"
                            )
                        }
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
                        onGridInfoChanged = { calendarGridPosition = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    if (uiState.pacientes.isNotEmpty()) {
                        PacienteChipsRow(
                            pacientes = uiState.pacientes,
                            onDragStart = { paciente, position ->
                                dragState = DragState(paciente, position)
                            },
                            onDragMove = { delta ->
                                dragState = dragState?.copy(
                                    position = dragState!!.position + delta
                                )
                            },
                            onDragEnd = {
                                val state = dragState
                                val targetDay = state?.let {
                                    getDropTargetDate(it.position, calendarGridPosition)
                                }
                                dragState = null
                                if (targetDay != null && state != null) {
                                    viewModel.onDateSelected(targetDay)
                                    viewModel.abrirCrearSesion(targetDay, state.paciente.id)
                                }
                            },
                            onDragCancel = { dragState = null }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    DayAppointmentsSection(
                        selectedDate = uiState.selectedDate,
                        sesiones = uiState.sesionesDelDia,
                        onDeleteSesion = viewModel::eliminarSesion,
                        onEditSesion = viewModel::abrirEditarSesion,
                        onAddSesion = viewModel::abrirCrearSesion
                    )
                }
            }
        }

        dragState?.let { state ->
            Card(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (state.position.x - 100).roundToInt(),
                            (state.position.y - 44).roundToInt()
                        )
                    }
                    .zIndex(10f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.paciente.nombre,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun PacienteChipsRow(
    pacientes: List<Paciente>,
    onDragStart: (Paciente, Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pacientes, key = { it.id }) { paciente ->
            PacienteDragChip(
                paciente = paciente,
                onDragStart = { pos -> onDragStart(paciente, pos) },
                onDragMove = onDragMove,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel
            )
        }
    }
}

@Composable
private fun PacienteDragChip(
    paciente: Paciente,
    onDragStart: (Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    var chipLayout by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Card(
        modifier = Modifier
            .onGloballyPositioned { chipLayout = it }
            .pointerInput(paciente.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        val windowPos = chipLayout?.localToWindow(offset)
                            ?: return@detectDragGesturesAfterLongPress
                        onDragStart(windowPos)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragMove(dragAmount)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = paciente.nombre,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun getDropTargetDate(
    dropPosition: Offset,
    gridInfo: CalendarGridInfo?
): LocalDate? {
    if (gridInfo == null) return null
    val localX = dropPosition.x - gridInfo.gridBounds.left
    val localY = dropPosition.y - gridInfo.gridBounds.top
    if (localX < 0 || localY < 0 ||
        localX > gridInfo.gridBounds.width ||
        localY > gridInfo.gridBounds.height
    ) return null
    val col = (localX / gridInfo.cellSize).toInt().coerceIn(0, 6)
    val row = (localY / gridInfo.cellSize).toInt()
    val cellIndex = row * 7 + col
    val dayNumber = cellIndex - gridInfo.firstDayOffset + 1
    if (dayNumber < 1 || dayNumber > gridInfo.daysInMonth) return null
    return gridInfo.yearMonth.atDay(dayNumber)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SesionDialog(
    dialogState: SesionDialogState,
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
            Text(text = if (dialogState.isEditing) "Editar sesión" else "Nueva sesión")
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
    onEditSesion: (Sesion) -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(sesiones, key = { it.id }) { sesion ->
                    SesionRow(
                        sesion = sesion,
                        onEdit = { onEditSesion(sesion) },
                        onDelete = { onDeleteSesion(sesion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SesionRow(
    sesion: Sesion,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sesion.horaInicio,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(52.dp)
            )

            if (sesion.horaFin.isNotBlank()) {
                Text(
                    text = "-${sesion.horaFin}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(56.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(56.dp))
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .padding(end = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = sesion.nombrePaciente,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar sesión",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar sesión",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
