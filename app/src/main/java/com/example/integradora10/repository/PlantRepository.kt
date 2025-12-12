package com.example.integradora10.repository

import com.example.integradora10.model.Plant
import com.example.integradora10.network.PlantApiService

/**
 * Repositorio de Plantas.
 *
 * Actúa como una capa de abstracción entre la fuente de datos (la API REST) y el ViewModel.
 * Centraliza las llamadas de red y proporciona métodos limpios y concisos al resto de la app.
 *
 * @param apiService Instancia de la interfaz Retrofit para el manejo de la API.
 */
class PlantRepository(private val apiService: PlantApiService) {

    /**
     * Obtiene la lista completa de plantas desde el servicio REST.
     * Corresponde a la operación READ (Leer).
     *
     * @return Lista de objetos [Plant].
     */
    suspend fun getPlants(): List<Plant> = apiService.getAllPlants()

    /**
     * Agrega una nueva planta al servidor.
     * Corresponde a la operación CREATE (Crear).
     *
     * @param plant El objeto [Plant] a ser creado.
     * @return El objeto [Plant] devuelto por el servidor con su ID.
     */
    suspend fun addPlant(plant: Plant): Plant = apiService.createPlant(plant)

    /**
     * Elimina una planta específica del servidor.
     * Corresponde a la operación DELETE (Eliminar).
     *
     * @param id El identificador único de la planta a eliminar.
     */
    suspend fun removePlant(id: Int) = apiService.deletePlant(id)

    /**
     * Actualiza un objeto [Plant] existente en el servidor.
     * Corresponde a la operación UPDATE (Actualizar).
     *
     * @param plant El objeto [Plant] con los datos modificados (incluye su ID).
     * @return El objeto [Plant] actualizado.
     */
    suspend fun updatePlant(plant: Plant): Plant {
        // Llama al servicio, usando el ID de la planta para la URL
        return apiService.updatePlant(plant.id, plant)
    }

}