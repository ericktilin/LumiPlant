package com.example.integradora10.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.integradora10.viewmodel.PlantViewModel
import androidx.compose.ui.platform.LocalDensity
import com.example.integradora10.model.Plant
import kotlinx.coroutines.launch

// Imports para la visualización de la imagen
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import android.net.Uri
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

// ----------------------------------------------------------------------------------
// FUNCIÓN DE UTILIDAD: Determina el color de estado basado en Lux
// ----------------------------------------------------------------------------------
private fun getLuxStatusColor(required: Float, current: Float): Color {
    val tolerance = required * 0.15f
    return when {
        current > required + tolerance -> Color(0xFFD32F2F) // Rojo (Exceso)
        current < required - tolerance -> Color(0xFFFBC02D) // Amarillo (Falta)
        else -> Color(0xFF388E3C) // Verde (Óptimo)
    }
}

// ----------------------------------------------------------------------------------
// PANTALLA PRINCIPAL DE LISTADO DE PLANTAS
// ----------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(viewModel: PlantViewModel, onNavigateToAdd: () -> Unit, onNavigateToEdit: (Int) -> Unit) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Estados para controlar el diálogo de confirmación de borrado
    var showDeleteDialog by remember { mutableStateOf(false) }
    var plantToDelete by remember { mutableStateOf<Plant?>(null) }

    // Función que se ejecuta al iniciar la pantalla: carga la lista de plantas
    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    Scaffold(
        // BARRA SUPERIOR (Muestra el estado actual del sensor de luz)
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Mis Plantas")
                        Spacer(Modifier.width(8.dp))
                        Text("(${viewModel.currentLight.toInt()} Lux ☀️)", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary))
                    }
                }
            )
        },
        // BOTÓN FLOTANTE (Para agregar nuevas plantas)
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Agregar planta")
            }
        }
    ) { padding ->
        // Bloque de carga (Muestra un indicador si los datos se están cargando)
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // key() fuerza la recreación del LazyColumn cuando la lista cambia, ayudando a refrescar la UI.
            key(viewModel.refreshKey) {
                // CONTENEDOR DE LA LISTA (Muestra todas las plantas)
                LazyColumn(modifier = Modifier.padding(padding)) {
                    // Itera sobre la lista de plantas del ViewModel
                    items(
                        items = viewModel.plants,
                        key = { plant -> plant.id.toString() }
                    ) { plant ->
                        val currentPlant by rememberUpdatedState(plant)

                        // Configuración de la funcionalidad de "deslizar para eliminar" (SwipeToDismiss)
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                    // Si se desliza, muestra el diálogo de confirmación de borrado
                                    plantToDelete = currentPlant
                                    showDeleteDialog = true
                                    false
                                } else {
                                    false
                                }
                            },
                            positionalThreshold = { with(density) { 150.dp.toPx() } }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = { DeleteBackground(dismissState) }, // Fondo rojo con ícono de bote de basura
                            content = {
                                // TARJETA ELEVADA (Diseño de cada ítem de la lista)
                                ElevatedCard(
                                    onClick = { plant.id.let { id -> onNavigateToEdit(id) } }, // Navega a la edición al hacer click
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    colors = CardDefaults.elevatedCardColors()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 1. ZONA DE IMAGEN (Muestra la foto de la planta o un ícono por defecto)
                                        val uri = plant.imageUrl?.let {
                                            try { Uri.parse(it) } catch (e: Exception) { null }
                                        }

                                        if (uri != null) {
                                            AsyncImage(
                                                model = uri,
                                                contentDescription = "Foto de ${plant.name}",
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape), // Imagen redonda
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            // Ícono por defecto
                                            Box(
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Sin foto",
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.width(16.dp))

                                        // 2. ZONA DE TEXTO (Nombre y Tipo)
                                        Column(
                                            modifier = Modifier.weight(1f) // Ocupa el espacio restante
                                        ) {
                                            Text(
                                                text = plant.name,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Text(text = "Tipo: ${plant.type}", style = MaterialTheme.typography.bodyMedium)
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        // 3. INDICADOR DE LUX REQUERIDA (Muestra el Lux Requerido como texto simple)
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${plant.requiredLux.toInt()} Lux",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Req.",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN DE BORRADO
    if (showDeleteDialog && plantToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                // Al tocar fuera, se cancela el borrado y se recarga la lista para resetear el swipe
                showDeleteDialog = false
                plantToDelete = null
                // LLAMADA NECESARIA si el usuario CANCELA el Swipe para resetear la tarjeta deslizada
                coroutineScope.launch {
                    viewModel.loadPlants()
                }
            },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la planta: ${plantToDelete!!.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        // 1. LLAMADA AL VIEWMODEL
                        plantToDelete!!.id.let { id ->
                            // Esta función ahora se encarga de borrar y luego de llamar a loadPlants()
                            viewModel.deletePlant(id)
                        }

                        // 2. CERRAR EL DIÁLOGO (¡No se necesita coroutineScope.launch aquí!)
                        showDeleteDialog = false
                        plantToDelete = null
                    }
                ) {
                    Text("Sí, Borrar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        plantToDelete = null
                        // LLAMADA NECESARIA si el usuario CANCELA el Swipe para resetear la tarjeta deslizada
                        coroutineScope.launch {
                            viewModel.loadPlants()
                        }
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}


// VISTA QUE APARECE AL DESLIZAR (Fondo Rojo con Bote de Basura)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DeleteBackground(dismissState: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.background
            SwipeToDismissBoxValue.EndToStart -> Color.Red
            SwipeToDismissBoxValue.StartToEnd -> Color.Red
        }, label = "ColorAnim"
    )

    val alignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Alignment.CenterEnd else Alignment.CenterStart
    val icon = Icons.Filled.Delete

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Borrar",
            tint = Color.White
        )
    }
}