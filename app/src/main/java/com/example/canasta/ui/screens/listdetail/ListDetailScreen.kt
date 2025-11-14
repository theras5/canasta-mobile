package com.example.canasta.ui.screens.listdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import com.example.canasta.ui.components.common.BottomNavBar
import com.example.canasta.ui.components.products.ListProduct
import com.example.canasta.ui.components.products.ProductItemCard
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles
import java.util.UUID

/**
 * Pantalla de detalle de una lista con sus productos
 *
 * @param listName Nombre de la lista
 * @param onBackClick Callback cuando se presiona el botón de volver
 * @param onShareClick Callback cuando se presiona compartir
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listName: String = "Casa",
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("Lacteos") }

    // Datos de ejemplo - en producción vendrían de un ViewModel
    var products by remember {
        mutableStateOf(
            listOf(
                ListProduct("1", "Leche", "2L, 2%", false),
                ListProduct("2", "Queso", "Cheddar", false),
                ListProduct("3", "Yogurt", "2L, Frutilla", false),
                ListProduct("4", "Crema", "500mL", false),
                ListProduct("5", "Queso", "Cheddar", true)
            )
        )
    }

    val categories = listOf("Lacteos", "Limpieza", "Harinas", "Verduras", "Carnes")

    // Función dummy para toggle del checkbox
    fun toggleProductCheck(product: ListProduct) {
        products = products.map {
            if (it.id == product.id) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }
        println("Producto ${product.name} - Checked: ${!product.isChecked}")
    }

    // Función dummy para eliminar producto
    fun deleteProduct(product: ListProduct) {
        products = products.filter { it.id != product.id }
        println("Producto eliminado: ${product.name}")
    }

    // Función dummy para agregar producto
    fun addProduct() {
        val newProduct = ListProduct(
            id = UUID.randomUUID().toString(),
            name = "Nuevo Producto",
            description = "Descripción",
            isChecked = false
        )
        products = products + newProduct
        println("Producto agregado: ${newProduct.name}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = listName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Titles
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Titles
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = Titles
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addProduct() },
                containerColor = Secondary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "Agregar producto")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Chips de categorías
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Secondary.copy(alpha = 0.3f),
                                    selectedLabelColor = Secondary,
                                    containerColor = Color(0xFFFFF8F0),
                                    labelColor = Color(0xFFD2691E)
                                )
                            )
                        }
                    }
                }

                // Lista de productos
                items(products) { product ->
                    ProductItemCard(
                        product = product,
                        onCheckedChange = { toggleProductCheck(product) },
                        onDelete = { deleteProduct(product) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListDetailScreenPreview() {
    MaterialTheme {
        ListDetailScreen()
    }
}

