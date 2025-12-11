package com.example.integradora10.ui

// ... (tus imports existentes)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.integradora10.model.Plant
import com.example.integradora10.viewmodel.PlantViewModel
// Asegúrate de tener estos imports:
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import java.io.File
import androidx.compose.foundation.rememberScrollState // Import para el estado del scroll
import androidx.compose.foundation.verticalScroll // Import para hacer scroll vertical
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    viewModel: PlantViewModel,
    plantIdToEdit: Int = 0, // 0 si es nueva planta, > 0 si es edición
    onNavigateBack: () -> Unit
) {
    // 1. ESTADOS LOCALES (Inputs del usuario y control de UI)
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var requiredLux by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    // ESTADOS Y CONTEXTO para la cámara/galería
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) } // URI (dirección) de la imagen seleccionada
    var recommendationMessage by remember { mutableStateOf("") }
    val currentLux = viewModel.currentLight

    // ESTADOS Y DATOS para el SCROLL y el Dropdown
    val scrollState = rememberScrollState() // Guarda la posición del scroll
    val coroutineScope = rememberCoroutineScope() // Permite ejecutar tareas asíncronas (como el scroll animado)

    // Opciones predefinidas para el tipo de planta (stock)
    val plantTypes = listOf("Interior", "Exterior", "Suculenta", "Helecho", "Cactus", "Bonsái", "Tropical", "Frutal")
    var expanded by remember { mutableStateOf(false) } // Controla si el dropdown está abierto

    // --- PREPARACIÓN PARA LA CÁMARA (Crea un archivo temporal para guardar la foto) ---
    val tmpFile = File.createTempFile("temp_plant_image", ".png", context.cacheDir).apply {
        createNewFile()
    }
    // Genera la URI que la cámara usará para guardar la foto
    val tmpUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tmpFile
    )

    // ----------------------------------------------------------------------
    // --- DEFINICIÓN DE LANZADORES DE ACTIVIDAD (Manejo de Cámara y Permisos) ---
    // ----------------------------------------------------------------------

    // LANZADOR A: Para tomar la foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            imageUri = tmpUri // Si la foto se tomó, asignamos la URI temporal
        }
    }

    // LANZADOR B: Para solicitar el permiso de la cámara al usuario
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(tmpUri) // Si concede, lanza la cámara
        } else {
            Toast.makeText(context, "El permiso de cámara es necesario para tomar fotos.", Toast.LENGTH_LONG).show()
        }
    }
    // ----------------------------------------------------------------------

    // 2. LÓGICA DE CARGA DE DATOS (Se ejecuta al entrar en modo edición)
    LaunchedEffect(plantIdToEdit) {
        if (plantIdToEdit > 0) {
            val plant = viewModel.getPlantById(plantIdToEdit)
            if (plant != null) {
                name = plant.name
                type = plant.type
                requiredLux = plant.requiredLux.toString()
                isEditing = true

                // Carga la URI de la imagen si ya existe una guardada
                if (plant.imageUrl != null) {
                    try {
                        imageUri = Uri.parse(plant.imageUrl)
                    } catch (e: Exception) {
                        imageUri = null
                    }
                }
            }
        }
    }

    // 3. FUNCIÓN DE GUARDADO (Crea o actualiza la planta y vuelve atrás)
    fun onSave() {
        val requiredLuxFloat = requiredLux.toFloatOrNull()

        if (name.isBlank() || type.isBlank() || requiredLuxFloat == null) {
            return
        }

        if (isEditing && plantIdToEdit > 0) {
            // MODO ACTUALIZAR (UPDATE)
            val currentPlant = viewModel.getPlantById(plantIdToEdit)
            val currentLuxValue = currentPlant?.currentLux ?: 0f
            // Decide si usa la imagen recién tomada (imageUri) o la guardada (imageUrl)
            val imageToSave = imageUri?.toString() ?: currentPlant?.imageUrl

            val updatedPlant = Plant(
                id = plantIdToEdit,
                name = name,
                type = type,
                requiredLux = requiredLuxFloat,
                currentLux = currentLuxValue,
                imageUrl = imageToSave // Guarda el URL de la imagen
            )
            viewModel.updatePlant(updatedPlant)
        } else {
            // MODO CREAR (CREATE)
            viewModel.createNewPlant(
                name = name,
                type = type,
                reqLux = requiredLuxFloat,
                imageUrl = imageUri?.toString() // Envía el URL de la imagen al ViewModel
            )
        }

        onNavigateBack() // Vuelve a la pantalla anterior
    }

    // 4. FUNCIÓN LÓGICA DE RECOMENDACIÓN (Calcula si la luz es óptima/excesiva/deficiente)
    fun generateRecommendation(required: Float, current: Float): String {
        // ... (Lógica de tolerancia de luz)
        val tolerance = required * 0.15f
        val minLux = required - tolerance
        val maxLux = required + tolerance

        return when {
            current > maxLux -> {
                "¡Alerta! Exceso de Luz (${current.toInt()} Lux). Mueve la planta a una zona con menos iluminación. Ideal: ${required.toInt()} Lux."
            }
            current < minLux -> {
                "¡Alerta! Falta de Luz (${current.toInt()} Lux). La luz es insuficiente. Mueve la planta a un lugar más iluminado. Ideal: ${required.toInt()} Lux."
            }
            else -> {
                "Nivel Óptimo. La luz actual (${current.toInt()} Lux) está dentro del rango ideal (${minLux.toInt()} - ${maxLux.toInt()} Lux)."
            }
        }
    }

    // 5. ESTRUCTURA DE LA VISTA COMPUESTA
    Scaffold(
        topBar = {
            // Barra superior de la pantalla
            TopAppBar(
                title = { Text(if (isEditing) "Editar Planta" else "Registrar Planta 🌿") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // Contenedor principal con scroll vertical aplicado
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState), // Habilita el scroll vertical
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ZONA DE VISTA PREVIA Y BOTÓN DE CÁMARA
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                // Vista previa de la imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = "Imagen de la planta", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Añadir foto", modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }

                // Botón para activar la cámara
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        if (isCameraPermissionGranted) {
                            cameraLauncher.launch(tmpUri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("Tomar Foto")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ZONA DE INPUTS AGRUPADA
            ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Datos de la Planta", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo: Nombre
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre de la planta") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo: Tipo de Planta (Dropdown con opciones predefinidas)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = type,
                            onValueChange = {},
                            label = { Text("Tipo de planta") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        // Menú desplegable con las opciones
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            plantTypes.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = { type = selectionOption; expanded = false },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo: Lux Requerida
                    OutlinedTextField(
                        value = requiredLux,
                        onValueChange = { requiredLux = it },
                        label = { Text("Lux Requerida (ej: 300)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Botón de Guardar
            Button(
                onClick = ::onSave,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = name.isNotBlank() && type.isNotBlank() && requiredLux.isNotBlank()
            ) {
                Text(if (isEditing) "Guardar Cambios" else "Guardar en la Nube", style = MaterialTheme.typography.titleMedium)
            }

            // ZONA DE CÁLCULO Y RECOMENDACIÓN (Solo visible en modo edición)
            if (isEditing) {
                Spacer(modifier = Modifier.height(32.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN DE CÁLCULO (Activa la recomendación y fuerza el scroll)
                Button(
                    onClick = {
                        val requiredLuxFloat = requiredLux.toFloatOrNull()
                        if (requiredLuxFloat != null) {
                            recommendationMessage = generateRecommendation(requiredLuxFloat, currentLux)

                            // Forzar scroll al final para ver el mensaje
                            coroutineScope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        } else {
                            recommendationMessage = "Por favor, ingresa un valor de Lux Requerida válido."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Calcular Nivel de Luz Actual")
                }

                // MOSTRAR RESULTADO DE RECOMENDACIÓN
                if (recommendationMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val color = if (recommendationMessage.contains("Óptimo")) Color(0xFF388E3C) else MaterialTheme.colorScheme.error

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = color.copy(alpha = 0.1f))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = "Medida del Sensor: ${currentLux.toInt()} Lux ☀️",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recommendationMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = color
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}