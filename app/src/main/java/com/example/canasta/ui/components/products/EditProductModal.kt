package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.components.common.CustomModal
import com.example.canasta.ui.theme.Secondary

/**
 * Modal para editar un producto existente en la lista
 *
 * @param product Producto a editar
 * @param onDismiss Callback al cerrar el modal sin guardar
 * @param onSave Callback al guardar los cambios (name, quantity)
 */
@Composable
fun EditProductModal(
    product: ListProduct,
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: String) -> Unit
) {
    var productName by remember { mutableStateOf(product.name) }
    var productQuantity by remember { mutableStateOf(product.description) }
    var nameError by remember { mutableStateOf(false) }

    CustomModal(
        title = "Editar Producto",
        onDismiss = onDismiss
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Campo de nombre
            OutlinedTextField(
                value = productName,
                onValueChange = {
                    productName = it
                    nameError = it.isBlank()
                },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("El nombre no puede estar vacío")
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de cantidad
            OutlinedTextField(
                value = productQuantity,
                onValueChange = { productQuantity = it },
                label = { Text("Cantidad") },
                placeholder = { Text("Ej: 2kg, 1L, 3 unidades") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (productName.isNotBlank()) {
                            onSave(productName.trim(), productQuantity.trim())
                        } else {
                            nameError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProductModalPreview() {
    MaterialTheme {
        EditProductModal(
            product = ListProduct(
                id = "1",
                name = "Leche",
                description = "2L",
                isChecked = false,
                isPurchased = false
            ),
            onDismiss = {},
            onSave = { _, _ -> }
        )
    }
}

