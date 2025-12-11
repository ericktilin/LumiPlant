package com.example.integradora10.model

/**
 * Esta es una 'data class'. En Kotlin se usa para
 * objetos que solo sirven para transportar datos.
 */
data class Plant(
    val id: Int = 0,           // El ID lo genera el servidor de Python
    val name: String,              // Nombre de la flor
    val type: String,              // Tipo de planta
    val requiredLux: Float,        // Luz que necesita (ideal)
    val currentLux: Float,         // Luz que mide el sensor (actual)
    val imageUrl: String? = null   // Dirección de la foto en internet
)