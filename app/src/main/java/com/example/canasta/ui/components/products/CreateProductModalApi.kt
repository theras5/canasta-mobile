package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.ui.components.common.ModalTextField
import com.example.canasta.ui.theme.Primary

/**
 * Bottom Sheet para crear un nuevo producto con datos de la API.
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var productName by remember { mutableStateOf("") }
    var selectedCategory by remember(categories) { mutableStateOf(categories.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con icono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Agregar Producto",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            ModalTextField(
                label = "Nombre del producto *",
                value = productName,
                onValueChange = { productName = it },
                placeholder = "Ej: Fideos"
            )

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
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Button(
                onClick = {
                    if (productName.isNotBlank() && selectedCategory != null) {
                        onCreateProduct(productName.trim(), selectedCategory!!.id, null)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = productName.isNotBlank() && selectedCategory != null
            ) {
                Text(text = "Crear")
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text(text = "Cancelar")
            }
        }
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

