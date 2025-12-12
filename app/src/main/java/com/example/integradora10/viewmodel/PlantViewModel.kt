package com.example.integradora10.viewmodel

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora10.model.Plant
import com.example.integradora10.repository.PlantRepository
import com.example.integradora10.network.RetrofitClient // Asegúrate de que esta importación sea correcta
import kotlinx.coroutines.launch
import retrofit2.HttpException
class PlantViewModel : ViewModel() {

    // Usamos el cliente de Retrofit para crear el repositorio
    private val repository = PlantRepository(RetrofitClient.apiService)

    // ESTADOS REACTIVOS
    var plants by mutableStateOf<List<Plant>>(emptyList())
    var isLoading by mutableStateOf(false)
    var currentLight by mutableStateOf(0f)

    // CLAVE DE REFRESCO: Se incrementa para forzar el redibujado del LazyColumn
    var refreshKey by mutableStateOf(0)

    // Cargar plantas (GET)
    fun loadPlants() {
        viewModelScope.launch {
            isLoading = true // Inicia la carga
            try {
                // PASO CLAVE: Forzamos la lista a vacía para garantizar el redibujado
                plants = emptyList()

                // Recibimos los datos de la API
                val loadedPlants = repository.getPlants()
                plants = loadedPlants

                // Al finalizar exitosamente, incrementamos la clave para refrescar la UI
                refreshKey++
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false // ¡Asegura que el estado de carga siempre termina!
            }
        }
    }

    // Borrar planta (DELETE)
    fun deletePlant(id: Int) {
        viewModelScope.launch {
            try {
                repository.removePlant(id)
                // CLAVE PARA ELIMINACIÓN INSTANTÁNEA:
                // Una vez borrado de la API, volvemos a cargar la lista actualizada.
                loadPlants()
                // loadPlants() se encarga de llamar a refreshKey++ y actualizar 'plants'

            } catch (e: HttpException) {
                e.printStackTrace()
                println("ERROR API AL ELIMINAR: Código ${e.code()}")
            }
            catch (e: Exception) {
                // Otros errores (red, parseo, etc.)
                e.printStackTrace()
                println("ERROR GENERAL AL ELIMINAR: ${e.message}")
            }
        }
    }

    // Crear nueva planta (POST)
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
                loadPlants() // Recarga la lista para ver la nueva planta
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualizar planta (PUT)
    fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            try {
                repository.updatePlant(plant)
                loadPlants() // Recarga la lista para ver los cambios
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualizar luz del sensor (Sensor)
    fun updateLight(lux: Float) {
        currentLight = lux
    }

    // Buscar planta por ID (Lógica UI)
    fun getPlantById(id: Int): Plant? {
        return plants.find { it.id == id }
    }
}