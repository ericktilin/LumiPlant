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
import retrofit2.HttpException

/**
 * ViewModel principal para la aplicación.
 *
 * Actúa como el puente entre la UI (Compose) y la capa de datos (Repository).
 * Maneja el estado de la lista de plantas y toda la lógica de negocio CRUD.
 */
class PlantViewModel : ViewModel() {

    // Cliente del Repositorio: Fuente de datos para la API.
    private val repository = PlantRepository(RetrofitClient.apiService)

    // ESTADOS REACTIVOS (Observables por la UI)

    /** Lista de plantas cargadas desde la API. Su cambio gatilla la actualización de la UI. */
    var plants by mutableStateOf<List<Plant>>(emptyList())

    /** Indicador de estado de carga para mostrar un CircularProgressIndicator en la UI. */
    var isLoading by mutableStateOf(false)

    /** Valor actual de luz medido por el sensor del dispositivo. */
    var currentLight by mutableStateOf(0f)

    /** * Clave numérica que se incrementa en cada recarga exitosa.
     * Se usa en el key() del LazyColumn para forzar el redibujado instantáneo de la lista.
     */
    var refreshKey by mutableStateOf(0)

    /**
     * Carga la lista de plantas del repositorio (GET).
     * Esta función es llamada para inicializar la lista o después de cualquier cambio (CRUD).
     */
    fun loadPlants() {
        viewModelScope.launch {
            isLoading = true
            try {
                // **CORRECCIÓN CLAVE:** Usamos .toList() para forzar una nueva referencia de objeto.
                plants = repository.getPlants().toList()
                // Notificamos a Compose que la estructura de la lista ha cambiado.
                refreshKey++
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Borra una planta específica del servidor (DELETE) y recarga la lista.
     *
     * @param id El identificador de la planta a eliminar.
     */
    fun deletePlant(id: Int) {
        viewModelScope.launch {
            try {
                repository.removePlant(id)
                // Llama a loadPlants() para la eliminación instantánea.
                loadPlants()
            } catch (e: HttpException) {
                // Manejo específico para errores de la API (404, 500, etc.)
                e.printStackTrace()
                println("ERROR API AL ELIMINAR: Código ${e.code()}")
            }
            catch (e: Exception) {
                // Manejo de errores de red o genéricos.
                e.printStackTrace()
                println("ERROR GENERAL AL ELIMINAR: ${e.message}")
            }
        }
    }

    /**
     * Crea un nuevo registro de planta en el servidor (POST) y recarga la lista.
     *
     * @param name Nombre de la planta.
     * @param type Tipo de planta.
     * @param reqLux Lux requerido.
     * @param imageUrl URI de la imagen.
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
     * Actualiza los datos de una planta existente en el servidor (PUT) y recarga la lista.
     *
     * @param plant El objeto [Plant] con los nuevos datos.
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
     * Actualiza el estado de la luz actual (Sensor) para ser mostrado en la UI.
     *
     * @param lux El valor flotante de la iluminación ambiental.
     */
    fun updateLight(lux: Float) {
        currentLight = lux
    }

    /**
     * Busca una planta por su ID en la lista actualmente cargada.
     *
     * @param id El ID de la planta a buscar.
     * @return El objeto [Plant] o null si no se encuentra.
     */
    fun getPlantById(id: Int): Plant? {
        return plants.find { it.id == id }
    }
}