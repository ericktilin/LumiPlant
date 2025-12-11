package com.example.integradora10.network

import com.example.integradora10.model.Plant
import retrofit2.http.*

interface PlantApiService {
    // GET: Obtener todas las plantas de Python (Leer)
    @GET("plants")
    suspend fun getAllPlants(): List<Plant>

    // POST: Mandar una planta nueva (Crear)
    @POST("plants")
    suspend fun createPlant(@Body plant: Plant): Plant

    // DELETE: Borrar una planta por su ID (Eliminar)
    @DELETE("plants/{id}")
    suspend fun deletePlant(@Path("id") id: Int)

    @PUT("/plants/{id}")
    suspend fun updatePlant(@Path("id") id: Int, @Body plant: Plant): Plant
}