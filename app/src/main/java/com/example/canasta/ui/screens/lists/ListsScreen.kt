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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.data.ShoppingListService
import com.example.canasta.ui.components.common.ConfirmationModal
import com.example.canasta.ui.components.lists.CreateListModal
import com.example.canasta.ui.components.lists.EmptyStateListas
import com.example.canasta.ui.components.lists.ListsGrid
import com.example.canasta.ui.components.lists.ShoppingList
import com.example.canasta.ui.theme.Secondary
import kotlinx.coroutines.launch

@Composable
fun ListsScreen(
    onNavigateToListDetail: (ShoppingList) -> Unit = {}
) {
    var showCreateModal by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<ShoppingList?>(null) }

    val lists by ShoppingListService.listsState.collectAsState()
    val scope = rememberCoroutineScope()

    // Al entrar en la pantalla, cargar listas desde la API
    LaunchedEffect(Unit) {
        ShoppingListService.refreshLists()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateModal = true },
                containerColor = Secondary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "Crear nueva lista")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Listas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 16.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (lists.isEmpty()) {
                    EmptyStateListas()
                } else {
                    ListsGrid(
                        lists = lists,
                        onListClick = { list -> onNavigateToListDetail(list) },
                        onToggleFavorite = { /* TODO implementar favorito */ },
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
                    ShoppingListService.createList(name, image)
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
