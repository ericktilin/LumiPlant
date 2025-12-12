package com.example.integradora10.network

import com.example.integradora10.model.Plant
import retrofit2.Response // Importación necesaria para el Response<Unit>
import retrofit2.http.*

/**
 * [V2] Interfaz de Retrofit para la API de Plantas.
 * Define las operaciones CRUD con documentación KDoc.
 */
interface PlantApiService {

    /**
     * Obtiene la lista completa de todas las plantas registradas.
     * Corresponde a la operación **READ (Leer)** del CRUD.
     */
    @GET("plants")
    suspend fun getAllPlants(): List<Plant>

    /**
     * Crea un nuevo registro de planta en el servidor.
     * Corresponde a la operación **CREATE (Crear)** del CRUD.
     *
     * @param plant El objeto Plant a ser agregado.
     * @return Plant El objeto Plant creado.
     */
    @POST("plants")
    suspend fun createPlant(@Body plant: Plant): Plant

    /**
     * Elimina un registro de planta específico del servidor.
     * Corresponde a la operación **DELETE (Eliminar)** del CRUD.
     *
     * @param id El ID único de la planta a eliminar.
     * @return Response<Unit> Respuesta para confirmar el éxito (ej. HTTP 204 No Content).
     */
    @DELETE("plants/{id}")
    suspend fun deletePlant(@Path("id") id: Int): Response<Unit>

    /**
     * Actualiza un registro de planta existente basado en su ID.
     * Corresponde a la operación **UPDATE (Actualizar)** del CRUD.
     *
     * @param id El ID de la planta a actualizar.
     * @param plant El objeto Plant con los datos actualizados.
     * @return Plant El objeto Plant actualizado.
     */
    @PUT("/plants/{id}")
    suspend fun updatePlant(@Path("id") id: Int, @Body plant: Plant): Plant
}