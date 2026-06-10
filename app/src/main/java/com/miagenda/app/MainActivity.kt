package com.miagenda.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.miagenda.app.ui.navigation.Screen
import com.miagenda.app.ui.screens.detail.DetallePacienteScreen
import com.miagenda.app.ui.screens.list.ListaPacientesScreen
import com.miagenda.app.ui.theme.AgendaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgendaTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ListaPacientes.route
    ) {
        composable(Screen.ListaPacientes.route) {
            ListaPacientesScreen(
                onNavigateToDetalle = { id ->
                    navController.navigate(Screen.DetallePaciente.createRoute(id))
                },
                onNavigateToNuevo = {
                    navController.navigate(Screen.DetallePaciente.newRoute())
                }
            )
        }

        composable(
            route = Screen.DetallePaciente.route,
            arguments = listOf(
                navArgument("pacienteId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getLong("pacienteId") ?: -1L
            DetallePacienteScreen(
                pacienteId = pacienteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
