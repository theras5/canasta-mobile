package com.example.canasta.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.canasta.ui.components.common.CategoryChips
import com.example.canasta.ui.components.products.CreateProductModal
import com.example.canasta.ui.components.products.ListProduct
import com.example.canasta.ui.components.products.ProductItemCard
import com.example.canasta.ui.theme.Secondary
import java.util.UUID

@Composable
fun ProductsScreen() {
    // Estado UI
    var showCreateModal by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Categorías de ejemplo
    val categories = listOf("Todos", "Lácteos", "Bebidas", "Snacks", "Limpieza")

    // Productos de ejemplo (simulación ViewModel)
    var products by remember {
        mutableStateOf(
            listOf(
                ListProduct("1", "Leche", "Entera 1L", false),
                ListProduct("2", "Queso", "Cheddar 200g", false),
                ListProduct("3", "Gaseosa", "Cola 2L", false),
                ListProduct("4", "Jabón", "Para ropa 800g", false),
                ListProduct("5", "Yogurt", "Frutilla 1L", true)
            )
        )
    }

    // Lógica
    fun toggleChecked(product: ListProduct) {
        products = products.map { if (it.id == product.id) it.copy(isChecked = !it.isChecked) else it }
    }

    fun deleteProduct(product: ListProduct) {
        products = products.filter { it.id != product.id }
    }

    fun addProduct(name: String, category: String) {
        val new = ListProduct(
            id = UUID.randomUUID().toString(),
            name = name,
            description = category,
            isChecked = false
        )
        products = products + new
    }

    val filtered = products.filter { p ->
        val matchesSearch = searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || selectedCategory == "Todos" || p.description.contains(selectedCategory!!, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateModal = true },
                containerColor = Secondary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            // Título
            Text(
                text = "Productos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp, bottom = 16.dp)
            )

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar") },
                placeholder = { Text("Buscar producto...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips de categorías
            CategoryChips(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de productos
            Box(modifier = Modifier.weight(1f)) {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(filtered, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onCheckedChange = { toggleChecked(product) },
                            onDelete = { deleteProduct(product) }
                        )
                    }
                }
            }
        }
    }

    // Modal creación producto
    if (showCreateModal) {
        CreateProductModal(
            categories = categories.drop(1), // excluir "Todos"
            onDismiss = { showCreateModal = false },
            onCreateProduct = { name, category ->
                addProduct(name, category)
                showCreateModal = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductsScreenPreview() {
    ProductsScreen()
}

