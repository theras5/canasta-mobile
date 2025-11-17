package com.example.canasta.ui.screens.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.canasta.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.example.canasta.ui.theme.Errors
import com.example.canasta.ui.theme.Success
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun ListsScreen(
    onNavigateToListDetail: (ShoppingList) -> Unit = {}
) {
    val context = LocalContext.current
    var showCreateModal by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<ShoppingList?>(null) }

    val lists by ShoppingListService.listsState.collectAsState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    var lastSnackbarIsSuccess by remember { mutableStateOf(false) }

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
                    androidx.compose.material3.Snackbar(
                        snackbarData = data,
                        containerColor = if (lastSnackbarIsSuccess) Success else Errors,
                        contentColor = Color.White
                    )
                }
            )
        },
        floatingActionButton = {
            CommonFab(
                iconRes = R.drawable.add_list,
                contentDescription = stringResource(R.string.create_new_list),
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
            CommonScreenHeader(title = stringResource(R.string.lists_title))

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
                        },
                        onToggleRecurring = { list ->
                            scope.launch {
                                try {
                                    ShoppingListService.toggleRecurring(list.id, !list.isRecurring)
                                    lastSnackbarIsSuccess = true
                                    val message = if (!list.isRecurring) {
                                        context.getString(R.string.list_marked_recurring)
                                    } else {
                                        context.getString(R.string.list_unmarked_recurring)
                                    }
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        duration = SnackbarDuration.Short
                                    )
                                } catch (e: Exception) {
                                    lastSnackbarIsSuccess = false
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.error_updating_list),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
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
                        lastSnackbarIsSuccess = true
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.list_created_success),
                            duration = SnackbarDuration.Short
                        )
                    } catch (e: HttpException) {
                        lastSnackbarIsSuccess = false
                        if (e.code() == 409) {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.error_list_exists),
                                duration = SnackbarDuration.Short
                            )
                        } else {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.error_creating_list, e.code()),
                                duration = SnackbarDuration.Short
                            )
                        }
                    } catch (e: Exception) {
                        lastSnackbarIsSuccess = false
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.error_network_list),
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
            title = stringResource(R.string.delete_list_title),
            message = stringResource(R.string.delete_list_message, listToDelete?.name ?: ""),
            onDismiss = {
                showDeleteConfirmation = false
                listToDelete = null
            },
            onConfirm = {
                listToDelete?.let { list ->
                    scope.launch {
                        try {
                            ShoppingListService.deleteList(list.id)
                            lastSnackbarIsSuccess = true
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.list_deleted_success),
                                duration = SnackbarDuration.Short
                            )
                        } catch (_: Exception) {
                            lastSnackbarIsSuccess = false
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.error_deleting_list),
                                duration = SnackbarDuration.Short
                            )
                        }
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
