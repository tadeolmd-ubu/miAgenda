package com.miagenda.app.ui.navigation

sealed class Screen(val route: String) {
    data object ListaPacientes : Screen("lista_pacientes")
    data object DetallePaciente : Screen("detalle_paciente/{pacienteId}") {
        fun createRoute(id: Long) = "detalle_paciente/$id"
        fun newRoute() = "detalle_paciente/-1"
        const val ARG_PACIENTE_ID = "pacienteId"
    }
}
