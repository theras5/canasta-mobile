package com.example.canasta.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Modal simple para ingresar texto
 *
 * @param title Título del modal
 * @param label Etiqueta del campo
 * @param placeholder Texto placeholder
 * @param onDismiss Callback al cerrar
 * @param onSave Callback al guardar el texto
 */
@Composable
fun SimpleTextInputModal(
    title: String,
    label: String,
    placeholder: String = "",
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    CustomModal(title = title, onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ModalTextField(
                label = label,
                value = text,
                onValueChange = { text = it },
                placeholder = placeholder
            )

            Spacer(modifier = Modifier.height(24.dp))

            ModalActionButton(text = "Guardar", onClick = {
                onSave(text)
                onDismiss()
            }, enabled = text.isNotBlank())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleTextInputModalPreview() {
    MaterialTheme {
        SimpleTextInputModal(
            title = "Agregar Nota",
            label = "Nota",
            placeholder = "Ej: Comprar leche",
            onDismiss = {},
            onSave = {}
        )
    }
}
