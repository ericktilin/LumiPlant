package com.example.integradora10.network

import com.example.integradora10.model.Plant
import retrofit2.http.*

/**
 * [V1] Interfaz de Retrofit para la API de Plantas.
 * Define las operaciones básicas (GET, POST, DELETE, PUT).
 */
interface PlantApiService {

    // GET: Obtener todas las plantas (Operación Leer)
    @GET("plants")
    suspend fun getAllPlants(): List<Plant>

    // POST: Mandar una planta nueva (Operación Crear)
    @POST("plants")
    suspend fun createPlant(@Body plant: Plant): Plant

    // DELETE: Borrar una planta por su ID (Operación Eliminar)
    @DELETE("plants/{id}")
    suspend fun deletePlant(@Path("id") id: Int)

    // PUT: Actualizar una planta por su ID (Operación Actualizar)
    @PUT("/plants/{id}")
    suspend fun updatePlant(@Path("id") id: Int, @Body plant: Plant): Plant
}