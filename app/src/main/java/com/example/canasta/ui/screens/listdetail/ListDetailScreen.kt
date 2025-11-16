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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.ui.components.common.ConfirmationModal
import com.example.canasta.ui.components.lists.AddProductToListBottomSheet
import com.example.canasta.ui.components.products.ListProduct
import com.example.canasta.ui.components.products.ProductItemCard
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle de una lista con sus productos
 * Soporta dos modos: VIEW (visualización) y EDIT (edición)
 *
 * @param listId ID de la lista a mostrar
 * @param listName Nombre inicial de la lista
 * @param viewModel ViewModel para gestionar el estado
 * @param onBackClick Callback cuando se presiona el botón de volver
 * @param onShareClick Callback cuando se presiona compartir
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    listName: String,
    viewModel: ListDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar la lista al iniciar la pantalla
    LaunchedEffect(listId) {
        viewModel.loadList(listId, listName)
    }

    // Estado para el modal de confirmación de eliminación
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ListProduct?>(null) }

    // Estado para el bottom sheet de agregar productos
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showAddProductSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Mostrar modal de confirmación si hay producto para eliminar
    if (showDeleteDialog && productToDelete != null) {
        ConfirmationModal(
            title = "Eliminar producto",
            message = "¿Estás seguro de que quieres eliminar ${productToDelete?.name}?",
            onDismiss = {
                showDeleteDialog = false
                productToDelete = null
            },
            onConfirm = {
                productToDelete?.let { product ->
                    viewModel.deleteProduct(product.id)
                }
                showDeleteDialog = false
                productToDelete = null
            }
        )
    }

    // Mostrar bottom sheet de agregar productos
    if (showAddProductSheet) {
        AddProductToListBottomSheet(
            sheetState = bottomSheetState,
            products = uiState.availableProducts,
            categories = uiState.availableCategories,
            addedProductNames = uiState.products.map { it.name }.toSet(),
            onDismiss = {
                scope.launch {
                    bottomSheetState.hide()
                    showAddProductSheet = false
                }
            },
            onAddProduct = { product ->
                viewModel.addProductToList(product)
                // No cerramos el bottom sheet para permitir agregar múltiples productos
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Si estamos en modo edición, mostrar TextField editable
                    if (uiState.screenMode == ScreenMode.EDIT) {
                        OutlinedTextField(
                            value = uiState.listName,
                            onValueChange = { viewModel.updateListName(it) },
                            modifier = Modifier.fillMaxWidth(0.6f),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Titles
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Secondary,
                                unfocusedIndicatorColor = Color.Gray
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            )
                        )
                    } else {
                        // Modo vista - solo texto
                        Text(
                            text = uiState.listName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Titles
                        )
                    }
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
                    // Botón de Editar/Guardar
                    IconButton(onClick = {
                        if (uiState.screenMode == ScreenMode.EDIT) {
                            // Validar antes de guardar
                            if (viewModel.validateListName()) {
                                viewModel.toggleEditMode()
                            }
                        } else {
                            viewModel.toggleEditMode()
                        }
                    }) {
                        Icon(
                            imageVector = if (uiState.screenMode == ScreenMode.EDIT)
                                Icons.Default.Done
                            else
                                Icons.Default.Edit,
                            contentDescription = if (uiState.screenMode == ScreenMode.EDIT)
                                "Guardar cambios"
                            else
                                "Editar lista",
                            tint = Titles
                        )
                    }

                    // Botón de Compartir (solo visible en modo vista)
                    if (uiState.screenMode == ScreenMode.VIEW) {
                        IconButton(onClick = onShareClick) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir",
                                tint = Titles
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            // FAB solo visible en modo vista
            if (uiState.screenMode == ScreenMode.VIEW) {
                FloatingActionButton(
                    onClick = {
                        showAddProductSheet = true
                    },
                    containerColor = Secondary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, "Agregar producto")
                }
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
                        items(uiState.categories) { category ->
                            FilterChip(
                                selected = uiState.selectedCategory == category,
                                onClick = { viewModel.selectCategory(category) },
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
                items(uiState.products) { product ->
                    ProductItemCard(
                        product = product,
                        isEditMode = uiState.screenMode == ScreenMode.EDIT,
                        onCheckedChange = {
                            // Solo permitir check/uncheck en modo vista
                            if (uiState.screenMode == ScreenMode.VIEW) {
                                viewModel.toggleProductCheck(product.id)
                            }
                        },
                        onEdit = {
                            // TODO: Implementar modal de edición con el tipo correcto
                        },
                        onDelete = {
                            // Solo permitir eliminar si no está comprado
                            if (!product.isPurchased) {
                                productToDelete = product
                                showDeleteDialog = true
                            }
                        }
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ListDetailScreenPreview() {
//    MaterialTheme {
//        ListDetailScreen(
//            listId = "preview-list-id",
//            listName = "Mi Lista de Compras"
//        )
//    }
//}
