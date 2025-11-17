package com.example.canasta.ui.screens.listdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.ui.components.common.CategoryChipsApi
import com.example.canasta.ui.components.common.ConfirmationModal
import com.example.canasta.ui.components.common.EditProductModal
import com.example.canasta.ui.components.products.ListProduct
import com.example.canasta.ui.components.products.ProductItemCard
import com.example.canasta.ui.theme.Background
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

/**
 * Pantalla de detalle de una lista con sus productos
 * Soporta dos modos: VIEW (visualización) y EDIT (edición)
 *
 * @param listId ID de la lista a mostrar
 * @param listName Nombre inicial de la lista
 * @param viewModel ViewModel para gestionar el estado
 * @param onBackClick Callback cuando se presiona el botón de volver
 * @param onShareClick Callback cuando se presiona compartir
 * @param onDeleteClick Callback cuando se elimina la lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    listName: String,
    viewModel: ListDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar la lista al iniciar la pantalla
    LaunchedEffect(listId) {
        viewModel.loadList(listId, listName)
    }

    // Estado para el modal de confirmación de eliminación de producto
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ListProduct?>(null) }

    // Estado para el modal de confirmación de eliminación de lista
    var showDeleteListDialog by remember { mutableStateOf(false) }

    // Estado para el modal de edición de producto
    var showEditDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ListProduct?>(null) }

    // TODO: Implementar bottom sheet de agregar productos cuando esté disponible
    // val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    // var showAddProductSheet by remember { mutableStateOf(false) }
    // val scope = rememberCoroutineScope()

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

    // Mostrar modal de confirmación para eliminar lista
    if (showDeleteListDialog) {
        ConfirmationModal(
            title = "Eliminar lista",
            message = "¿Estás seguro de que quieres eliminar la lista \"${uiState.listName}\"? Esta acción no se puede deshacer.",
            onDismiss = {
                showDeleteListDialog = false
            },
            onConfirm = {
                viewModel.deleteList()
                showDeleteListDialog = false
                onDeleteClick()
            }
        )
    }

    // Mostrar modal de edición de producto
    if (showEditDialog && productToEdit != null) {
        EditProductModal(
            productName = productToEdit?.name ?: "",
            currentDescription = productToEdit?.description ?: "",
            onDismiss = {
                showEditDialog = false
                productToEdit = null
            },
            onConfirm = { newDescription ->
                productToEdit?.let { product ->
                    viewModel.updateProductQuantity(product.id, newDescription)
                }
                showEditDialog = false
                productToEdit = null
            }
        )
    }

    // TODO: Implementar bottom sheet de agregar productos
    // Mostrar bottom sheet de agregar productos
    /*
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
    */


    Scaffold(
        containerColor = Background,
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

                    // Botón de Eliminar (solo visible en modo vista)
                    if (uiState.screenMode == ScreenMode.VIEW) {
                        IconButton(onClick = {
                            showDeleteListDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar lista",
                                tint = Titles
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        floatingActionButton = {
            // TODO: Re-enable FAB when AddProductBottomSheet is implemented
            /*
            // FAB solo visible en modo vista
            if (uiState.screenMode == ScreenMode.VIEW) {
                CommonFab(
                    iconRes = R.drawable.add_item,
                    contentDescription = "Agregar producto",
                    onClick = { showAddProductSheet = true }
                )
            }
            */
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
                    CategoryChipsApi(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { category ->
                            viewModel.selectCategory(category)
                        }
                    )
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
                            productToEdit = product
                            showEditDialog = true
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
