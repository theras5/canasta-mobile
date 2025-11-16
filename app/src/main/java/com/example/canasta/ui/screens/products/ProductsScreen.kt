package com.example.canasta.ui.screens.products

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.data.remote.models.Product
import com.example.canasta.data.repository.CategoryRepository
import com.example.canasta.ui.components.common.CategoryChips
import com.example.canasta.ui.components.products.CreateProductModalApi
import com.example.canasta.ui.components.products.EditProductModal
import com.example.canasta.ui.components.products.RemoteProductCard
import com.example.canasta.ui.components.common.ConfirmDeleteModal
import com.example.canasta.ui.theme.Secondary

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Estados del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val products by viewModel.products.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Estados locales de UI
    var showCreateModal by remember { mutableStateOf(false) }
    var showEditModal by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<GetCategory?>(null) }
    var categories by remember { mutableStateOf<List<GetCategory>>(emptyList()) }
    var showDeleteModal by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // SnackbarHost para mensajes
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar categorías al iniciar
    LaunchedEffect(Unit) {
        loadCategories { loadedCategories ->
            categories = loadedCategories
        }
    }

    // Mostrar errores en Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // Filtrar productos
    val filteredProducts = products.filter { product ->
        val matchesSearch = searchQuery.isBlank() ||
            product.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null ||
            product.category?.id == selectedCategory!!.id
        matchesSearch && matchesCategory
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        if (isLandscape) {
            // En landscape: Scroll unificado de todo el contenido
            ProductsScreenLandscape(
                innerPadding = innerPadding,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                uiState = uiState,
                filteredProducts = filteredProducts,
                onEditClick = { product ->
                    productToEdit = product
                    showEditModal = true
                },
                onDeleteClick = { product ->
                    productToDelete = product
                    showDeleteModal = true
                },
                onRetry = { viewModel.loadProducts() }
            )
        } else {
            // En portrait: LazyColumn con scroll eficiente
            ProductsScreenPortrait(
                innerPadding = innerPadding,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                uiState = uiState,
                filteredProducts = filteredProducts,
                onEditClick = { product ->
                    productToEdit = product
                    showEditModal = true
                },
                onDeleteClick = { product ->
                    productToDelete = product
                    showDeleteModal = true
                },
                onRetry = { viewModel.loadProducts() }
            )
        }
    }

    // Modal creación producto
    if (showCreateModal) {
        CreateProductModalApi(
            categories = categories,
            onDismiss = { showCreateModal = false },
            onCreateProduct = { name, categoryId, _ ->
                viewModel.createProduct(name, categoryId, null)
                showCreateModal = false
            }
        )
    }

    // Modal edición producto
    if (showEditModal && productToEdit != null) {
        EditProductModal(
            product = productToEdit!!,
            categories = categories,
            onDismiss = {
                showEditModal = false
                productToEdit = null
            },
            onUpdateProduct = { productId, name, categoryId ->
                viewModel.updateProduct(productId, name, categoryId, null)
                showEditModal = false
                productToEdit = null
            }
        )
    }

    // Modal confirmación eliminación
    if (showDeleteModal && productToDelete != null) {
        ConfirmDeleteModal(
            title = "Eliminar Producto",
            message = "¿Seguro que querés eliminar \"${productToDelete!!.name}\"? Esta acción no se puede revertir.",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            onConfirm = {
                viewModel.deleteProduct(productToDelete!!.id)
                productToDelete = null
            },
            onDismiss = {
                showDeleteModal = false
                productToDelete = null
            }
        )
    }
}

// Composable para modo Portrait - con LazyColumn eficiente
@Composable
private fun ProductsScreenPortrait(
    innerPadding: PaddingValues,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<GetCategory>,
    selectedCategory: GetCategory?,
    onCategorySelected: (GetCategory?) -> Unit,
    uiState: ProductsUiState,
    filteredProducts: List<Product>,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp)
    ) {
        // Título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Productos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar") },
            placeholder = { Text("Buscar producto...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Chips de categorías
        if (categories.isNotEmpty()) {
            CategoryChipsApi(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Contenido principal con LazyColumn
        Box(modifier = Modifier.weight(1f)) {
            when (uiState) {
                is ProductsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProductsUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar productos",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FloatingActionButton(
                            onClick = onRetry,
                            containerColor = Secondary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reintentar",
                                tint = Color.White
                            )
                        }
                    }
                }
                is ProductsUiState.Success -> {
                    if (filteredProducts.isEmpty()) {
                        Text(
                            text = if (searchQuery.isBlank() && selectedCategory == null) {
                                "No hay productos disponibles"
                            } else {
                                "No se encontraron productos con los filtros aplicados"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 88.dp)
                        ) {
                            items(filteredProducts, key = { it.id }) { product ->
                                RemoteProductCard(
                                    product = product,
                                    onEditClick = onEditClick,
                                    onDeleteClick = onDeleteClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Composable para modo Landscape - con scroll unificado
@Composable
private fun ProductsScreenLandscape(
    innerPadding: PaddingValues,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<GetCategory>,
    selectedCategory: GetCategory?,
    onCategorySelected: (GetCategory?) -> Unit,
    uiState: ProductsUiState,
    filteredProducts: List<Product>,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // Título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Productos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar") },
            placeholder = { Text("Buscar producto...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Chips de categorías
        if (categories.isNotEmpty()) {
            CategoryChipsApi(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Contenido principal con scroll unificado
        when (uiState) {
            is ProductsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProductsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar productos",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = onRetry,
                        containerColor = Secondary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reintentar",
                            tint = Color.White
                        )
                    }
                }
            }
            is ProductsUiState.Success -> {
                if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank() && selectedCategory == null) {
                                "No hay productos disponibles"
                            } else {
                                "No se encontraron productos con los filtros aplicados"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 88.dp)
                    ) {
                        filteredProducts.forEach { product ->
                            RemoteProductCard(
                                product = product,
                                onEditClick = onEditClick,
                                onDeleteClick = onDeleteClick
                            )
                        }
                    }
                }
            }
        }
    }
}

// Función auxiliar para cargar categorías
private suspend fun loadCategories(onCategoriesLoaded: (List<GetCategory>) -> Unit) {
    val categoryRepository = CategoryRepository()
    try {
        categoryRepository.getCategories().fold(
            onSuccess = { categories -> onCategoriesLoaded(categories) },
            onFailure = { onCategoriesLoaded(emptyList()) }
        )
    } catch (_: Exception) { // silenciar warning
        onCategoriesLoaded(emptyList())
    }
}

// Composable para chips de categorías con la API
@Composable
private fun CategoryChipsApi(
    categories: List<GetCategory>,
    selectedCategory: GetCategory?,
    onCategorySelected: (GetCategory?) -> Unit
) {
    val categoryNames = listOf("Todos") + categories.map { it.name }
    CategoryChips(
        categories = categoryNames,
        selectedCategory = selectedCategory?.name ?: "Todos",
        onCategorySelected = { categoryName ->
            val category = if (categoryName == "Todos") null else categories.find { it.name == categoryName }
            onCategorySelected(category)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ProductsScreenPreview() {
    ProductsScreen()
}
