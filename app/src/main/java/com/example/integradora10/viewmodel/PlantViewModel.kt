package com.example.integradora10.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora10.model.Plant
import com.example.integradora10.repository.PlantRepository
import com.example.integradora10.network.RetrofitClient // Asegúrate de que esta importación sea correcta
import kotlinx.coroutines.launch

/**
 * [V1] ViewModel base: Solo incluye la carga de datos inicial y la gestión del estado de carga.
 * Las funciones CRUD (POST, PUT, DELETE) y la lógica de corrección se añadirán después.
 */
class PlantViewModel : ViewModel() {

    private val repository = PlantRepository(RetrofitClient.apiService)

    // ESTADOS REACTIVOS
    var plants by mutableStateOf<List<Plant>>(emptyList())
    var isLoading by mutableStateOf(false)
    var currentLight by mutableStateOf(0f)

    // CLAVE DE REFRESCO y funciones CRUD omitidas en la V1
    // var refreshKey by mutableStateOf(0)

    /**
     * Carga la lista de plantas del repositorio (GET).
     * Esta es la funcionalidad mínima requerida.
     */
    fun loadPlants() {
        viewModelScope.launch {
            isLoading = true
            try {
                plants = repository.getPlants()
            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, la lista queda vacía.
            } finally {
                isLoading = false
            }
        }
    }

    // Función de actualización de luz (sensor), también esencial.
    fun updateLight(lux: Float) {
        currentLight = lux
    }

    }