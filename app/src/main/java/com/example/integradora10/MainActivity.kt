package com.example.integradora10

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.integradora10.ui.AddPlantScreen
import com.example.integradora10.ui.PlantListScreen
import com.example.integradora10.viewmodel.PlantViewModel

class MainActivity : ComponentActivity(), SensorEventListener {
    private val plantViewModel: PlantViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            MaterialTheme {
                // CAMBIO CLAVE: Usamos un Int? para guardar el ID de la planta a editar.
                // Si es null, estamos agregando. Si es Int, estamos editando ese ID.
                var plantIdToEdit by remember { mutableStateOf<Int?>(null) }
                // Estado de navegación simple
                if (plantIdToEdit == null) {

                    // Manejamos dos acciones desde PlantListScreen:
                    PlantListScreen(
                        viewModel = plantViewModel,
                        // 1. Navegar a AGREGAR (pasamos null)
                        onNavigateToAdd = { plantIdToEdit = 0 }, // Usamos 0 o cualquier Int no nulo para entrar en modo 'Add'
                        // 2. Navegar a EDITAR (pasamos el ID)
                        onNavigateToEdit = { id -> plantIdToEdit = id } // Nueva función para editar
                    )
                } else {
                    // Si plantIdToEdit NO es null, vamos a la pantalla de Agregar/Editar
                    AddPlantScreen(
                        viewModel = plantViewModel,
                        plantIdToEdit = plantIdToEdit!!, // <-- Pasamos el ID a editar
                        onNavigateBack = { plantIdToEdit = null }
                    )
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            plantViewModel.updateLight(lux)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}