package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.data.remote.models.Product
import com.example.canasta.ui.components.common.CustomModal
import com.example.canasta.ui.components.common.ModalActionButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.canasta.ui.components.common.ModalTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.fillMaxWidth

/**
 * Modal para editar un producto existente con datos de la API.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductModal(
    product: Product,
    categories: List<GetCategory>,
    onDismiss: () -> Unit,
    onUpdateProduct: (productId: Long, name: String, categoryId: Long?) -> Unit
) {
    var productName by remember { mutableStateOf(product.name) }
    var selectedCategory by remember(categories, product) { mutableStateOf(product.category ?: categories.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    CustomModal(
        title = "Editar Producto",
        onDismiss = onDismiss
    ) {
        // Nombre
        ModalTextField(
            label = "Nombre del producto *",
            value = productName,
            onValueChange = { productName = it },
            placeholder = "Ej: Fideos"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ciclar categorías (último paso limpia categoría => null)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (categories.isNotEmpty()) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory?.name ?: "Seleccione una categoría",
                onValueChange = {},
                readOnly = true,
                enabled = categories.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                label = { Text("Categoría *") },
                placeholder = { Text("Seleccione una categoría") },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            selectedCategory = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ModalActionButton(
            text = "Guardar",
            onClick = {
                if (productName.isNotBlank() && selectedCategory != null) {
                    onUpdateProduct(product.id, productName.trim(), selectedCategory!!.id)
                }
            },
            enabled = productName.isNotBlank() && selectedCategory != null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProductModalPreview() {
    MaterialTheme {
        EditProductModal(
            product = Product(
                id = 1L,
                name = "Aceite de Oliva",
                metadata = null,
                createdAt = "2023-01-01T00:00:00Z",
                updatedAt = "2023-01-01T00:00:00Z",
                category = GetCategory(
                    id = 1L,
                    name = "Condimentos",
                    metadata = mapOf("icon" to "shopping_cart"),
                    createdAt = "2023-01-01T00:00:00Z",
                    updatedAt = "2023-01-01T00:00:00Z"
                )
            ),
            categories = listOf(
                GetCategory(1L, "Condimentos", mapOf("icon" to "shopping_cart"), "2023-01-01T00:00:00Z", "2023-01-01T00:00:00Z"),
                GetCategory(2L, "Bebidas", mapOf("icon" to "shopping_cart"), "2023-01-01T00:00:00Z", "2023-01-01T00:00:00Z")
            ),
            onDismiss = {},
            onUpdateProduct = { _, _, _ -> }
        )
    }
}
