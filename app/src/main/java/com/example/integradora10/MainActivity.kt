package com.example.integradora10

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.integradora10.ui.AddPlantScreen
import com.example.integradora10.ui.PlantListScreen
import com.example.integradora10.ui.theme.Integradora10Theme
import com.example.integradora10.viewmodel.PlantViewModel

/**
 * Actividad Principal: Configura la UI, la Navegación y gestiona el Sensor de Luz.
 * Implementa [SensorEventListener] para recibir datos del sensor de luz.
 */
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private lateinit var plantViewModel: PlantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de Sensores (Servicio y Sensor de Luz)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            Integradora10Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlantAppNavigation()
                }
            }
        }
    }

    /**
     * Se llama cuando el sensor de luz detecta un cambio.
     * Actualiza el ViewModel con el nuevo valor de Lux.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val luxValue = event.values[0]
            // Solo actualiza si el ViewModel ya está inicializado.
            if (::plantViewModel.isInitialized) {
                plantViewModel.updateLight(luxValue)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se requiere implementación específica.
    }

    /**
     * Activa el sensor cuando la actividad pasa a primer plano.
     */
    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /**
     * Desactiva el sensor cuando la actividad se detiene para ahorrar batería.
     */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @Composable
    fun PlantAppNavigation() {
        val navController = rememberNavController()
        // Inicializa el ViewModel y lo asigna a la variable lateinit.
        plantViewModel = viewModel<PlantViewModel>()

        // Gestiona el ciclo de vida del sensor dentro de Compose:
        // Asegura que onResume/onPause se llamen correctamente cuando la composición entra/sale.
        DisposableEffect(Unit) {
            onResume()
            onDispose {
                onPause()
            }
        }

        NavHost(navController = navController, startDestination = "plantList") {
            composable("plantList") {
                PlantListScreen(
                    viewModel = plantViewModel,
                    onNavigateToAdd = { navController.navigate("addPlant/0") },
                    onNavigateToEdit = { id -> navController.navigate("addPlant/$id") }
                )
            }

            composable(
                route = "addPlant/{plantId}",
                arguments = listOf(navArgument("plantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getInt("plantId") ?: 0

                AddPlantScreen(
                    viewModel = plantViewModel,
                    plantIdToEdit = plantId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}