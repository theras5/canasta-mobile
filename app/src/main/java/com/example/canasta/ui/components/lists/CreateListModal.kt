package com.example.canasta.ui.components.lists

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.components.common.CustomModal
import com.example.canasta.ui.components.common.ModalActionButton
import com.example.canasta.ui.components.common.ModalDropdown
import com.example.canasta.ui.components.common.ModalTextField

/**
 * Modal para crear una nueva lista
 * 
 * @param onDismiss Callback cuando se cierra el modal
 * @param onCreateList Callback cuando se crea la lista con nombre e imagen seleccionada
 */
@Composable
fun CreateListModal(
    onDismiss: () -> Unit,
    onCreateList: (name: String, imageIcon: String?) -> Unit
) {
    var listName by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    
    CustomModal(
        title = "Agregar",
        onDismiss = onDismiss
    ) {
        // Campo de nombre
        ModalTextField(
            label = "Nombre de la lista",
            value = listName,
            onValueChange = { listName = it },
            placeholder = "Ej: Casa"
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Selector de imagen
        ModalDropdown(
            label = "Imagen de la lista",
            selectedValue = selectedImage,
            placeholder = "Seleccione una imagen",
            onClick = { 
                // TODO: Implementar selector de imagen
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Botón de acción
        ModalActionButton(
            text = "Agregar",
            onClick = {
                if (listName.isNotBlank()) {
                    onCreateList(listName, selectedImage)
                    onDismiss()
                }
            },
            enabled = listName.isNotBlank()
        )
    }
}

