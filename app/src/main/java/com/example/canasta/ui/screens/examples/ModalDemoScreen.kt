package com.example.canasta.ui.screens.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.components.common.ConfirmationModal
import com.example.canasta.ui.components.common.SimpleDropdownModal
import com.example.canasta.ui.components.common.SimpleTextInputModal
import com.example.canasta.ui.components.lists.CreateListModal

/**
 * Pantalla de demostración de los diferentes tipos de modales
 * Esta pantalla muestra cómo usar todos los modales disponibles
 */
@Composable
fun ModalDemoScreen() {
    var showCreateListModal by remember { mutableStateOf(false) }
    var showSimpleTextModal by remember { mutableStateOf(false) }
    var showConfirmationModal by remember { mutableStateOf(false) }
    var showDropdownModal by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Demostración de Modales",
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Botón para CreateListModal
            Button(
                onClick = { showCreateListModal = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mostrar Modal Crear Lista")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para SimpleTextInputModal
            Button(
                onClick = { showSimpleTextModal = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mostrar Modal Texto Simple")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ConfirmationModal
            Button(
                onClick = { showConfirmationModal = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mostrar Modal Confirmación")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para DropdownModal
            Button(
                onClick = { showDropdownModal = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mostrar Modal Dropdown")
            }
        }
    }

    // Modales
    if (showCreateListModal) {
        CreateListModal(
            onDismiss = { showCreateListModal = false },
            onCreateList = { name, image ->
                println("Lista creada: $name con imagen: $image")
            }
        )
    }

    if (showSimpleTextModal) {
        SimpleTextInputModal(
            title = "Agregar Nota",
            label = "Nota",
            placeholder = "Ej: Comprar leche",
            onDismiss = { showSimpleTextModal = false },
            onSave = { text ->
                println("Texto guardado: $text")
            }
        )
    }

    if (showConfirmationModal) {
        ConfirmationModal(
            title = "Confirmar",
            message = "¿Estás seguro de realizar esta acción?",
            onDismiss = { showConfirmationModal = false },
            onConfirm = {
                println("Acción confirmada")
            }
        )
    }

    if (showDropdownModal) {
        SimpleDropdownModal(
            title = "Seleccionar Categoría",
            label = "Categoría",
            placeholder = "Seleccione una categoría",
            selectedValue = selectedCategory,
            onDismiss = { showDropdownModal = false },
            onDropdownClick = {
                // Aquí mostrarías un diálogo para seleccionar la categoría
                selectedCategory = "Frutas" // Ejemplo
            },
            onConfirm = { category ->
                println("Categoría seleccionada: $category")
            }
        )
    }
}

