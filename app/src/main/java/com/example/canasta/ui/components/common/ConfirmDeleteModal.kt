package com.example.canasta.ui.components.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.theme.Errors

@Composable
fun ConfirmDeleteModal(
    title: String = "Eliminar",
    message: String = "Esta acción eliminará el elemento definitivamente y no se puede deshacer.",
    confirmText: String = "Eliminar",
    dismissText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    CustomModal(title = title, onDismiss = onDismiss) {
        Text(message)
        Spacer(modifier = Modifier.height(24.dp))
        ModalActionButton(
            text = confirmText,
            onClick = {
                onConfirm()
                onDismiss()
            },
            backgroundColor = Errors  // Botón rojo para acción crítica
        )
        Spacer(modifier = Modifier.height(8.dp))
        ModalActionButton(
            text = dismissText,
            onClick = { onDismiss() },
            isSecondary = true
        )
    }
}
