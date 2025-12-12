package com.example.integradora10.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora10.model.Plant
import com.example.integradora10.repository.PlantRepository
import com.example.integradora10.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException // Necesaria para el manejo de errores específicos de la API

/**
 * [V2] ViewModel final con todas las funcionalidades CRUD, manejo de estados
 * y correcciones para el borrado instantáneo en Jetpack Compose.
 */
class PlantViewModel : ViewModel() {

    private val repository = PlantRepository(RetrofitClient.apiService)

    // ESTADOS REACTIVOS
    var plants by mutableStateOf<List<Plant>>(emptyList())
    var isLoading by mutableStateOf(false)
    var currentLight by mutableStateOf(0f)

    // CLAVE DE REFRESCO: Se incrementa para forzar el redibujado del LazyColumn
    var refreshKey by mutableStateOf(0)

    /**
     * Carga la lista de plantas del repositorio (GET).
     * Incluye la corrección .toList() y refreshKey para garantizar el refresco de Compose.
     */
    fun loadPlants() {
        viewModelScope.launch {
            isLoading = true
            try {
                // **CORRECCIÓN CLAVE:** Usamos .toList() para forzar una nueva referencia de objeto.
                plants = repository.getPlants().toList()
                refreshKey++
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Borra una planta específica (DELETE) y llama a loadPlants() para recargar la lista.
     */
    fun deletePlant(id: Int) {
        viewModelScope.launch {
            try {
                repository.removePlant(id)
                // Llama a loadPlants() para la eliminación instantánea.
                loadPlants()
            } catch (e: HttpException) {
                e.printStackTrace()
                println("ERROR API AL ELIMINAR: Código ${e.code()}")
            }
            catch (e: Exception) {
                e.printStackTrace()
                println("ERROR GENERAL AL ELIMINAR: ${e.message}")
            }
        }
    }

    /**
     * Crea una nueva planta (POST) y recarga la lista.
     */
    fun createNewPlant(name: String, type: String, reqLux: Float, imageUrl: String?) {
        viewModelScope.launch {
            try {
                val newPlant = Plant(
                    name = name,
                    type = type,
                    requiredLux = reqLux,
                    currentLux = 0f,
                    imageUrl = imageUrl
                )
                repository.addPlant(newPlant)
                loadPlants()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Actualiza una planta existente (PUT) y recarga la lista.
     */
    fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            try {
                repository.updatePlant(plant)
                loadPlants()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Actualiza el estado de la luz actual (Sensor).
     */
    fun updateLight(lux: Float) {
        currentLight = lux
    }

    /**
     * Busca una planta por su ID en la lista.
     */
    fun getPlantById(id: Int): Plant? {
        return plants.find { it.id == id }
    }
}