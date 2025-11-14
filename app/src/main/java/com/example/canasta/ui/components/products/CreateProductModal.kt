package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.components.common.CustomModal
import com.example.canasta.ui.components.common.ModalActionButton
import com.example.canasta.ui.components.common.ModalDropdown
import com.example.canasta.ui.components.common.ModalTextField

/**
 * Modal para crear un nuevo producto con nombre y categoría.
 * @param categories Lista de categorías disponibles.
 * @param onDismiss Callback al cerrar.
 * @param onCreateProduct Callback con nombre y categoría cuando se crea.
 */
@Composable
fun CreateProductModal(
    categories: List<String>,
    onDismiss: () -> Unit,
    onCreateProduct: (name: String, category: String) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    CustomModal(
        title = "Agregar Producto",
        onDismiss = onDismiss
    ) {
        // Nombre del producto
        ModalTextField(
            label = "Nombre del producto",
            value = productName,
            onValueChange = { productName = it },
            placeholder = "Ej: Leche"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Categoría
        ModalDropdown(
            label = "Categoría",
            selectedValue = selectedCategory,
            placeholder = "Seleccione una categoría",
            onClick = {
                // Simulación sencilla: ciclar categorías en cada click si no hay un selector complejo
                val currentIndex = categories.indexOf(selectedCategory)
                selectedCategory = if (currentIndex == -1 || currentIndex == categories.lastIndex) categories.first() else categories[currentIndex + 1]
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ModalActionButton(
            text = "Agregar",
            onClick = {
                if (productName.isNotBlank() && selectedCategory != null) {
                    onCreateProduct(productName.trim(), selectedCategory!!)
                    onDismiss()
                }
            },
            enabled = productName.isNotBlank() && selectedCategory != null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateProductModalPreview() {
    MaterialTheme {
        CreateProductModal(
            categories = listOf("Lácteos", "Bebidas", "Snacks"),
            onDismiss = {},
            onCreateProduct = { _, _ -> }
        )
    }
}

