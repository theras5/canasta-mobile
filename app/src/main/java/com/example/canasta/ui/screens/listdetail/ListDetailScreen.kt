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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.R
import com.example.canasta.ui.components.common.CategoryChipsApi
import com.example.canasta.ui.components.common.CommonFab
import com.example.canasta.ui.components.common.ConfirmationModal
import com.example.canasta.ui.components.common.EditProductModal
import com.example.canasta.ui.components.lists.AddProductToListBottomSheet
import com.example.canasta.ui.components.lists.EmptyStateListDetail
import com.example.canasta.ui.components.products.ListProduct
import com.example.canasta.ui.components.products.ProductItemCard
import com.example.canasta.ui.theme.Background
import com.example.canasta.ui.theme.Errors
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Success
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
 * @param onDeleteClick Callback cuando se elimina la lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    listName: String,
    viewModel: ListDetailViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as android.app.Application
        )
    ),
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar la lista al iniciar la pantalla
    LaunchedEffect(listId) {
        viewModel.loadList(listId, listName)
    }

    // Detectar cuando la lista debe ser eliminada automáticamente y navegar de vuelta
    LaunchedEffect(uiState.shouldDeleteAndNavigateBack) {
        println("DEBUG Screen: shouldDeleteAndNavigateBack cambió a ${uiState.shouldDeleteAndNavigateBack}")
        if (uiState.shouldDeleteAndNavigateBack) {
            println("DEBUG Screen: Navegando de vuelta...")
            viewModel.resetDeleteAndNavigateBack()
            onBackClick()
        }
    }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    var lastMessageWasSuccess by remember { mutableStateOf(false) }

    // Mostrar mensajes de error
    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            lastMessageWasSuccess = false
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    // Mostrar mensajes de éxito
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            lastMessageWasSuccess = true
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
    }

    // Estado para el modal de confirmación de eliminación de producto
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ListProduct?>(null) }

    // Estado para el modal de confirmación de eliminación de lista
    var showDeleteListDialog by remember { mutableStateOf(false) }

    // Estado para el modal de edición de producto
    var showEditDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ListProduct?>(null) }

    // Lista de unidades disponibles similar a la web
    val units = listOf("unidades", "kg", "gr", "lt", "ml", "paquete")

    // Estado para el bottom sheet de compartir
    var showShareSheet by remember { mutableStateOf(false) }

    // Estado para agregar productos
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddProductSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Mostrar modal de confirmación si hay producto para eliminar
    if (showDeleteDialog && productToDelete != null) {
        ConfirmationModal(
            title = stringResource(R.string.delete_product_title_list),
            message = stringResource(R.string.delete_product_message_list, productToDelete?.name ?: ""),
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
            title = stringResource(R.string.delete_list_title),
            message = stringResource(R.string.delete_list_confirm_message, uiState.listName),
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
        val description = productToEdit?.description ?: ""
        val parts = description.trim().split(" ")
        val initialQuantity = parts.firstOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 1.0
        val initialUnit = if (parts.size > 1) parts.drop(1).joinToString(" ") else "unidades"

        EditProductModal(
            productName = productToEdit?.name ?: "",
            initialQuantity = initialQuantity,
            initialUnit = initialUnit,
            units = units,
            onDismiss = {
                showEditDialog = false
                productToEdit = null
            },
            onConfirm = { newQuantity, newUnit ->
                productToEdit?.let { product ->
                    viewModel.updateProductQuantity(product.id, newQuantity, newUnit)
                }
                showEditDialog = false
                productToEdit = null
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

    // Mostrar bottom sheet de compartir
    if (showShareSheet) {
        com.example.canasta.ui.components.lists.ShareListBottomSheet(
            sharedUsers = uiState.sharedUsers,
            isLoading = uiState.isSharing,
            errorMessage = uiState.shareError,
            onDismissRequest = {
                showShareSheet = false
                viewModel.clearShareError()
            },
            onShareWithEmail = { email ->
                viewModel.shareListWithEmail(email)
            },
            onRevokeAccess = { user ->
                viewModel.revokeShareAccess(user)
            }
        )
    }


    Scaffold(
        containerColor = Background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    androidx.compose.material3.Snackbar(
                        snackbarData = data,
                        containerColor = if (lastMessageWasSuccess) Success else Errors,
                        contentColor = Color.White
                    )
                }
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    // Si estamos en modo edición Y somos owner, mostrar TextField editable
                    if (uiState.screenMode == ScreenMode.EDIT && uiState.isOwner) {
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
                        // Modo vista o no es owner - solo texto
                        Text(
                            text = uiState.listName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Titles
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Si estamos en modo edición, primero cancelar la edición (sin guardar)
                        if (uiState.screenMode == ScreenMode.EDIT) {
                            viewModel.cancelEdit()
                        } else {
                            // Si estamos en modo vista, navegar hacia atrás
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (uiState.screenMode == ScreenMode.EDIT) {
                                stringResource(R.string.cancel)
                            } else {
                                stringResource(R.string.back)
                            },
                            tint = Titles
                        )
                    }
                },
                actions = {
                    // Botón de Editar/Guardar (visible para todos)
                    IconButton(onClick = {
                        if (uiState.screenMode == ScreenMode.EDIT) {
                            // Si es owner, validar antes de guardar
                            if (uiState.isOwner && !viewModel.validateListName()) {
                                return@IconButton
                            }
                            viewModel.toggleEditMode()
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
                                stringResource(R.string.save_changes_list)
                            else
                                stringResource(R.string.edit_list),
                            tint = Titles
                        )
                    }

                    // Botón de Compartir (solo visible en modo vista y si es owner)
                    if (uiState.screenMode == ScreenMode.VIEW && uiState.isOwner) {
                        IconButton(onClick = {
                            viewModel.loadSharedUsers()
                            showShareSheet = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share),
                                tint = Titles
                            )
                        }
                    }

                    // Botón de Eliminar (solo visible en modo vista y si es owner)
                    if (uiState.screenMode == ScreenMode.VIEW && uiState.isOwner) {
                        IconButton(onClick = {
                            showDeleteListDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_list),
                                tint = Errors
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
            // FAB solo visible en modo vista
            if (uiState.screenMode == ScreenMode.VIEW) {
                CommonFab(
                    iconRes = R.drawable.add_item,
                    contentDescription = stringResource(R.string.add_product_to_list),
                    onClick = { showAddProductSheet = true }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiState.products.isEmpty()) {
                // Mostrar estado vacío cuando no hay productos
                EmptyStateListDetail()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
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
}
