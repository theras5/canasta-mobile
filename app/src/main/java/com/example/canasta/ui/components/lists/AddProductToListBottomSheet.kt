package com.example.canasta.ui.components.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.data.remote.models.Product
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

/**
 * Bottom sheet para agregar productos a una lista
 * Usa chips para filtrar por categorías
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductToListBottomSheet(
    sheetState: SheetState,
    products: List<Product>,
    categories: List<GetCategory>,
    addedProductNames: Set<String>, // Nombres de productos ya agregados
    onDismiss: () -> Unit,
    onAddProduct: (Product) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<GetCategory?>(null) }

    // Filtrar productos que NO están ya en la lista
    // No usar remember aquí para que se recalcule automáticamente cuando cambie addedProductNames
    val availableProducts = products.filter { product ->
        !addedProductNames.contains(product.name)
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        sheetMaxWidth = androidx.compose.ui.unit.Dp.Unspecified,
        scrimColor = Color.Black.copy(alpha = 0.1f), // Scrim muy sutil
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // Limitar altura al 50% de la pantalla
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = {
                    Text(
                        "Encontrá tus Productos",
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedContainerColor = Color(0xFFFFF8F0),
                    unfocusedContainerColor = Color(0xFFFFF8F0)
                ),
                singleLine = true
            )

            // Chips de categorías
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Chip "Todos"
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todos") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Secondary.copy(alpha = 0.3f),
                            selectedLabelColor = Secondary,
                            containerColor = Color(0xFFFFF8F0),
                            labelColor = Color(0xFFD2691E)
                        )
                    )
                }

                // Chips de categorías
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory?.id == category.id,
                        onClick = {
                            selectedCategory = if (selectedCategory?.id == category.id) null else category
                        },
                        label = { Text(category.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Secondary.copy(alpha = 0.3f),
                            selectedLabelColor = Secondary,
                            containerColor = Color(0xFFFFF8F0),
                            labelColor = Color(0xFFD2691E)
                        )
                    )
                }
            }

            // Lista de productos filtrados
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Filtrar productos disponibles por búsqueda y categoría
                val filteredProducts = availableProducts.filter { product ->
                    val matchesSearch = searchQuery.isBlank() ||
                        product.name.contains(searchQuery, ignoreCase = true)
                    val matchesCategory = selectedCategory == null ||
                        product.category?.id == selectedCategory?.id
                    matchesSearch && matchesCategory
                }

                if (filteredProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron productos",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    items(filteredProducts) { product ->
                        ProductItemRow(
                            product = product,
                            onAddProduct = onAddProduct
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila de producto individual
 */
@Composable
private fun ProductItemRow(
    product: Product,
    onAddProduct: (Product) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFEEEEEE),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            // Botón de agregar - más grande y sin círculo de fondo
            IconButton(
                onClick = { onAddProduct(product) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar ${product.name}",
                    tint = Secondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

