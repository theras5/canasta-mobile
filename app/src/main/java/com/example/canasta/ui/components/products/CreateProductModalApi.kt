package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.ui.components.common.CustomModal
import com.example.canasta.ui.components.common.ModalActionButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.canasta.ui.components.common.ModalTextField

/**
 * Modal para crear un nuevo producto con datos de la API.
 * @param categories Lista de categorías disponibles de la API.
 * @param onDismiss Callback al cerrar.
 * @param onCreateProduct Callback con nombre y ID de categoría cuando se crea.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductModalApi(
    categories: List<GetCategory>,
    onDismiss: () -> Unit,
    onCreateProduct: (name: String, categoryId: Long?, metadata: Map<String, String>?) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var selectedCategory by remember(categories) { mutableStateOf(categories.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    CustomModal(
        title = "Agregar Producto",
        onDismiss = onDismiss
    ) {
        // Nombre del producto
        ModalTextField(
            label = "Nombre del producto *",
            value = productName,
            onValueChange = { productName = it },
            placeholder = "Ej: Fideos"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categoría
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
            text = "Crear",
            onClick = {
                if (productName.isNotBlank() && selectedCategory != null) {
                    onCreateProduct(productName.trim(), selectedCategory!!.id, null)
                }
            },
            enabled = productName.isNotBlank() && selectedCategory != null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateProductModalApiPreview() {
    MaterialTheme {
        CreateProductModalApi(
            categories = listOf(
                GetCategory(
                    id = 1L,
                    name = "Lácteos",
                    metadata = mapOf("icon" to "restaurant"),
                    createdAt = "2023-01-01T00:00:00Z",
                    updatedAt = "2023-01-01T00:00:00Z"
                ),
                GetCategory(
                    id = 2L,
                    name = "Bebidas",
                    metadata = mapOf("icon" to "water_drop"),
                    createdAt = "2023-01-01T00:00:00Z",
                    updatedAt = "2023-01-01T00:00:00Z"
                )
            ),
            onDismiss = {},
            onCreateProduct = { _, _, _ -> }
        )
    }
}
