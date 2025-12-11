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

class PlantViewModel : ViewModel() {
    // Usamos el cliente de Retrofit para crear el repositorio
    private val repository = PlantRepository(RetrofitClient.apiService)

    // Estados para Compose
    var plants by mutableStateOf<List<Plant>>(emptyList())
    var isLoading by mutableStateOf(false)
    var currentLight by mutableStateOf(0f)

    // Cargar plantas desde Python (GET)
    fun loadPlants() {
        viewModelScope.launch {
            isLoading = true
            try {
                plants = repository.getPlants()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    var refreshKey by mutableStateOf(0)
    // Borrar planta (DELETE)
    fun deletePlant(id: Int) {
        viewModelScope.launch {
            try {
                repository.removePlant(id)
                loadPlants() // Recargamos la lista después de borrar
                refreshKey++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun createNewPlant(name: String, type: String, reqLux: Float, imageUrl: String?) {
        viewModelScope.launch {
            try {
                val newPlant = Plant(
                    name = name,
                    type = type,
                    requiredLux = reqLux,
                    currentLux = 0f,
                    imageUrl = imageUrl // <--- ¡CAMBIO CLAVE: AÑADIDO EL URL!
                )
                repository.addPlant(newPlant)
                loadPlants() // Recarga la lista para que aparezca la nueva
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualizar luz del sensor
    fun updateLight(lux: Float) {
        currentLight = lux
    }

    fun getPlantById(id: Int): Plant? {
        // Busca la planta en la lista actual. Si no la encuentra, devuelve null.
        return plants.find { it.id == id }
    }
    // Dentro de PlantViewModel
    fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            try {
                // El repositorio debe hacer la llamada PUT al API
                repository.updatePlant(plant)
                loadPlants() // Recarga la lista para que se vean los cambios
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
