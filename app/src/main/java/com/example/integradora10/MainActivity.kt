package com.example.integradora10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.integradora10.ui.PlantListScreen
import com.example.integradora10.ui.theme.Integradora10Theme
import com.example.integradora10.viewmodel.PlantViewModel

/**
 * [INICIAL] Actividad Principal sin funcionalidad de sensores ni navegación completa.
 * Solo configura el entorno de Jetpack Compose.
 */
class MainActivity : ComponentActivity() { // NO hereda de SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No hay inicialización de sensores.

        setContent {
            Integradora10Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llama a la composición principal.
                    PlantAppContent()
                }
            }
        }
    }

    /**
     * Contenido Mínimo de la aplicación, solo con la pantalla de lista.
     * La lógica de navegación de edición/agregar se omite para este commit.
     */
    @Composable
    fun PlantAppContent() {
        val navController = rememberNavController()
        val plantViewModel: PlantViewModel = viewModel()

        // Configuración de NavHost con la ruta principal.
        NavHost(navController = navController, startDestination = "plantList") {
            composable("plantList") {
                PlantListScreen(
                    viewModel = plantViewModel,
                    // Las funciones de navegación no hacen nada por ahora.
                    onNavigateToAdd = { /* TODO */ },
                    onNavigateToEdit = { /* TODO */ }
                )
            }
            // Las otras rutas composables (addPlant) se omiten.
        }
    }
}