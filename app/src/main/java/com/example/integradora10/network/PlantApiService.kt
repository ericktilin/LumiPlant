package com.example.integradora10.network

import com.example.integradora10.model.Plant
import retrofit2.http.*

/**
 * Interfaz de Servicio REST para la gestión de datos de Plantas.
 * Define los endpoints para las operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * que interactúan con el backend de Python/Flask.
 */
interface PlantApiService {

    /**
     * Obtiene la lista completa de todas las plantas registradas en el servidor.
     * Corresponde a la operación **READ (Leer)** del CRUD.
     *
     * @return List<Plant> La lista de objetos Plant.
     */
    @GET("plants")
    suspend fun getAllPlants(): List<Plant>

    /**
     * Crea un nuevo registro de planta en el servidor.
     * Corresponde a la operación **CREATE (Crear)** del CRUD.
     *
     * @param plant El objeto Plant a ser agregado (el ID es ignorado o autogenerado).
     * @return Plant El objeto Plant creado, incluyendo el ID asignado por el servidor.
     */
    @POST("plants")
    suspend fun createPlant(@Body plant: Plant): Plant

    /**
     * Elimina un registro de planta específico del servidor.
     * Corresponde a la operación **DELETE (Eliminar)** del CRUD.
     *
     * @param id El ID único de la planta a eliminar.
     * @return Unit Una respuesta de éxito (generalmente código 204 No Content).
     */
    @DELETE("plants/{id}")
    suspend fun deletePlant(@Path("id") id: Int)

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