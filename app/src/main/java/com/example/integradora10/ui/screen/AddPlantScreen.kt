package com.example.integradora10.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.integradora10.viewmodel.PlantViewModel

/**
 * [V1] Pantalla inicial para agregar o editar una planta.
 * Contiene el diseño básico de los campos de texto, pero la lógica de guardado es mínima.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    viewModel: PlantViewModel,
    plantIdToEdit: Int, // ID de la planta a editar (0 si es nueva)
    onNavigateBack: () -> Unit
) {
    // Estados básicos de los campos de texto
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var requiredLux by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Si estamos editando, intenta cargar la planta
    val isEditing = plantIdToEdit != 0
    val plantToEdit = remember(plantIdToEdit) { viewModel.getPlantById(plantIdToEdit) }

    // Inicialización simple de campos
    LaunchedEffect(plantToEdit) {
        if (plantToEdit != null) {
            name = plantToEdit.name
            type = plantToEdit.type
            requiredLux = plantToEdit.requiredLux.toString()
            imageUrl = plantToEdit.imageUrl ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEditing) "Editar Planta" else "Nueva Planta") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Campos de texto básicos (sin validación)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
            OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo") })
            OutlinedTextField(value = requiredLux, onValueChange = { requiredLux = it }, label = { Text("Lux Requerido") })
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL Imagen (Opcional)") })

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de Guardar (con lógica mínima)
            Button(
                onClick = {
                    // Lógica MÍNIMA: solo regresa, no implementa guardado real
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar Cambios" else "Crear Planta")
            }
        }
    }
}