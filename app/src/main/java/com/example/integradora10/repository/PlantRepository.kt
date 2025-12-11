package com.example.integradora10.repository

import com.example.integradora10.model.Plant
import com.example.integradora10.network.PlantApiService

class PlantRepository(private val apiService: PlantApiService) {

    // Función para obtener la lista (GET)
    suspend fun getPlants(): List<Plant> = apiService.getAllPlants()

    // Función para crear (POST)
    suspend fun addPlant(plant: Plant): Plant = apiService.createPlant(plant)

    // Función para borrar (DELETE) - Asegúrate de que coincida con PlantApiService
    suspend fun removePlant(id: Int) = apiService.deletePlant(id)

    suspend fun updatePlant(plant: Plant): Plant {
        // Llama al servicio, usando el ID de la planta para la URL
        return apiService.updatePlant(plant.id, plant)
    }
}
