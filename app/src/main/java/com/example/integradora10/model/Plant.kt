package com.example.integradora10.model

/**
 * Modelo de datos (Data Class) para la entidad Planta.
 *
 * Esta clase se utiliza para el intercambio de información (JSON)
 * entre el cliente Android y el servidor backend de Python.
 */
data class Plant(
    /**
     * Identificador único de la planta.
     * Es clave primaria y generalmente autogenerado por el servidor (Python).
     * Valor por defecto 0 para las plantas que se van a crear.
     */
    val id: Int = 0,

    /**
     * Nombre descriptivo o común de la planta.
     */
    val name: String,

    /**
     * Categoría o tipo de planta (e.g., Suculenta, Interior, Helecho).
     */
    val type: String,

    /**
     * Nivel de iluminación (Lux) ideal o requerido para esta especie.
     */
    val requiredLux: Float,

    /**
     * Último valor de Lux medido por el sensor del dispositivo en la ubicación de la planta.
     * Se inicializa en 0f al crear el objeto.
     */
    val currentLux: Float = 0f,

    /**
     * URL (local o remota) de la imagen tomada de la planta.
     * Es un campo opcional (nullable String).
     */
    val imageUrl: String? = null
)