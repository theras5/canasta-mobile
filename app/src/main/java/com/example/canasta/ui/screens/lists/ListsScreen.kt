package com.example.canasta.ui.screens.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.canasta.ui.components.common.ConfirmationModal
import com.example.canasta.ui.components.lists.CreateListModal
import com.example.canasta.ui.components.lists.EmptyStateListas
import com.example.canasta.ui.components.lists.ListsGrid
import com.example.canasta.ui.components.lists.ShoppingList
import com.example.canasta.ui.components.lists.TopBarListas
import com.example.canasta.ui.theme.Secondary
import java.util.UUID

@Composable
fun ListsScreen(
    onNavigateToListDetail: (ShoppingList) -> Unit = {}
) {
    var showCreateModal by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<ShoppingList?>(null) }

    // Datos de ejemplo - en producción vendrían de un ViewModel
    var lists by remember {
        mutableStateOf(
            listOf(
                ShoppingList("1", "Casa", 38, "🏠", true),
                ShoppingList("2", "Supermercado", 15, "🛒", false),
                ShoppingList("3", "Farmacia", 7, "💊", true)
            )
        )
    }

    // Función dummy para crear una lista
    fun createList(name: String, icon: String?) {
        val newList = ShoppingList(
            id = UUID.randomUUID().toString(),
            name = name,
            productCount = 0,
            icon = icon ?: "📋",
            isFavorite = false
        )
        lists = lists + newList
        println("Lista creada: $name con ID: ${newList.id}")
    }

    // Función dummy para eliminar una lista
    fun deleteList(list: ShoppingList) {
        lists = lists.filter { it.id != list.id }
        println("Lista eliminada: ${list.name} con ID: ${list.id}")
    }

    // Función dummy para marcar/desmarcar como favorito
    fun toggleFavorite(list: ShoppingList) {
        lists = lists.map {
            if (it.id == list.id) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        println("Toggle favorito: ${list.name} - Favorito: ${!list.isFavorite}")
    }

    Scaffold(
        topBar = { TopBarListas() },
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
        Box(modifier = Modifier.padding(innerPadding)) {
            if (lists.isEmpty()) {
                EmptyStateListas()
            } else {
                ListsGrid(
                    lists = lists,
                    onListClick = { list ->
                        onNavigateToListDetail(list)
                    },
                    onToggleFavorite = { list ->
                        toggleFavorite(list)
                    },
                    onDeleteList = { list ->
                        listToDelete = list
                        showDeleteConfirmation = true
                    }
                )
            }
        }
    }
    
    // Modal para crear lista
    if (showCreateModal) {
        CreateListModal(
            onDismiss = { showCreateModal = false },
            onCreateList = { name, image ->
                createList(name, image)
                showCreateModal = false
            }
        )
    }

    // Modal de confirmación para eliminar
    if (showDeleteConfirmation && listToDelete != null) {
        ConfirmationModal(
            title = "Eliminar Lista",
            message = "¿Estás seguro de que deseas eliminar la lista \"${listToDelete?.name}\"? Esta acción no se puede deshacer.",
            onDismiss = {
                showDeleteConfirmation = false
                listToDelete = null
            },
            onConfirm = {
                listToDelete?.let { deleteList(it) }
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
