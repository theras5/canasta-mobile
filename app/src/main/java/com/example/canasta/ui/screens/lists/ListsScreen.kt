package com.example.canasta.ui.screens.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.data.ShoppingListService
import com.example.canasta.ui.components.common.CommonFab
import com.example.canasta.ui.components.common.CommonScreenHeader
import com.example.canasta.ui.components.common.ConfirmationModal
import com.example.canasta.ui.components.lists.CreateListModal
import com.example.canasta.ui.components.lists.EmptyStateListas
import com.example.canasta.ui.components.lists.ListsGrid
import com.example.canasta.ui.components.lists.ShoppingList
import com.example.canasta.ui.theme.Background
import com.example.canasta.ui.theme.Secondary
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun ListsScreen(
    onNavigateToListDetail: (ShoppingList) -> Unit = {}
) {
    var showCreateModal by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<ShoppingList?>(null) }

    val lists by ShoppingListService.listsState.collectAsState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    // Al entrar en la pantalla, cargar listas desde la API
    LaunchedEffect(Unit) {
        ShoppingListService.refreshLists()
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    // Snackbar rojo para errores
                    androidx.compose.material3.Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFFB00020), // rojo de error
                        contentColor = Color.White
                    )
                }
            )
        },
        floatingActionButton = {
            CommonFab(
                icon = Icons.Filled.Add,
                contentDescription = "Crear nueva lista",
                onClick = { showCreateModal = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            CommonScreenHeader(title = "Listas")

            Box(modifier = Modifier.weight(1f)) {
                if (lists.isEmpty()) {
                    EmptyStateListas()
                } else {
                    ListsGrid(
                        lists = lists,
                        onListClick = { list -> onNavigateToListDetail(list) },
                        onDeleteList = { list ->
                            listToDelete = list
                            showDeleteConfirmation = true
                        }
                    )
                }
            }
        }
    }

    if (showCreateModal) {
        CreateListModal(
            onDismiss = { showCreateModal = false },
            onCreateList = { name, image ->
                scope.launch {
                    try {
                        ShoppingListService.createList(name, image)
                        // Si llega aquí, la creación fue exitosa y refreshLists() ya se llamó en el servicio
                    } catch (e: HttpException) {
                        if (e.code() == 409) {
                            snackbarHostState.showSnackbar(
                                message = "Ya existe una lista con ese nombre.",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Error al crear la lista (${e.code()}). Intenta de nuevo.",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(
                            message = "Error de red al crear la lista. Revisa tu conexión.",
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                showCreateModal = false
            }
        )
    }

    if (showDeleteConfirmation && listToDelete != null) {
        ConfirmationModal(
            title = "Eliminar Lista",
            message = "¿Estás seguro de que deseas eliminar la lista \"${listToDelete?.name}\"? Esta acción no se puede deshacer.",
            onDismiss = {
                showDeleteConfirmation = false
                listToDelete = null
            },
            onConfirm = {
                listToDelete?.let { list ->
                    scope.launch {
                        ShoppingListService.deleteList(list.id)
                    }
                }
                showDeleteConfirmation = false
                listToDelete = null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ListsScreenPreview() {
    ListsScreen()
}
