package com.miagenda.app.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miagenda.app.domain.mapper.toEntity
import com.miagenda.app.ui.components.EmptyState
import com.miagenda.app.ui.components.PacienteCard
import com.miagenda.app.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPacientesScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateToNuevo: () -> Unit,
    viewModel: ListaPacientesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agenda de Pacientes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.pacientes.isEmpty() && uiState.searchQuery.isBlank() -> {
                    EmptyState(
                        mensaje = "No hay pacientes registrados.\nPresiona + para agregar uno."
                    )
                }
                uiState.pacientes.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                    EmptyState(
                        mensaje = "No se encontraron pacientes\ncon \"${uiState.searchQuery}\""
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = uiState.pacientes,
                            key = { it.id }
                        ) { paciente ->
                            PacienteCard(
                                paciente = paciente.toEntity(),
                                onClick = { onNavigateToDetalle(paciente.id) },
                                onDelete = { viewModel.eliminarPaciente(paciente.toEntity()) }
                            )
                        }
                    }
                }
            }
        }
    }
}
