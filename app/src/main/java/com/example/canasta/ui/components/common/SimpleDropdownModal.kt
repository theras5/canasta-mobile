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
 * Modal simple para seleccionar un valor desde un dropdown (simulado)
 *
 * @param title Título del modal
 * @param label Etiqueta del selector
 * @param placeholder Placeholder
 * @param selectedValue Valor actualmente seleccionado
 * @param onDismiss Callback al cerrar
 * @param onDropdownClick Callback para abrir el selector (puede abrir otra UI)
 * @param onConfirm Callback al confirmar la selección
 */
@Composable
fun SimpleDropdownModal(
    title: String,
    label: String,
    placeholder: String = "",
    selectedValue: String?,
    onDismiss: () -> Unit,
    onDropdownClick: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var currentSelection by remember { mutableStateOf(selectedValue) }

    CustomModal(title = title, onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ModalDropdown(
                label = label,
                selectedValue = currentSelection,
                placeholder = placeholder,
                onClick = onDropdownClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            ModalActionButton(text = "Confirmar", onClick = {
                currentSelection?.let { onConfirm(it) }
                onDismiss()
            }, enabled = currentSelection != null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleDropdownModalPreview() {
    MaterialTheme {
        SimpleDropdownModal(
            title = "Seleccionar Categoría",
            label = "Categoría",
            placeholder = "Seleccione una categoría",
            selectedValue = null,
            onDismiss = {},
            onDropdownClick = {},
            onConfirm = {}
        )
    }
}
