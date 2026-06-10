package com.miagenda.app.ui.navigation

sealed class Screen(val route: String) {
    data object ListaPacientes : Screen("lista_pacientes")
    data object DetallePaciente : Screen("detalle_paciente/{pacienteId}") {
        fun createRoute(id: Long) = "detalle_paciente/$id"
        fun newRoute() = "detalle_paciente/-1"
        const val ARG_PACIENTE_ID = "pacienteId"
    }
    data object TodosPacientes : Screen("todos_pacientes")
    data object HistorialPaciente : Screen("historial_paciente/{pacienteId}") {
        fun createRoute(id: Long) = "historial_paciente/$id"
        const val ARG_PACIENTE_ID = "pacienteId"
    }
    data object Ajustes : Screen("ajustes")
}
