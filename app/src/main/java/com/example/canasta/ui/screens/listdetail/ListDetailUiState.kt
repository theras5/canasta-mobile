package com.example.canasta.ui.screens.listdetail

import com.example.canasta.data.model.ShoppingList
import com.example.canasta.data.model.ShoppingListItem

data class ListDetailUiState(
    val shoppingList: ShoppingList? = null,
    val items: List<ShoppingListItem> = emptyList(),
    val completedItems: List<ShoppingListItem> = emptyList(),
    val pendingItems: List<ShoppingListItem> = emptyList(),
    val isLoading: Boolean = false,
    val isFetching: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val showAddItemModal: Boolean = false,
    val showEditItemModal: Boolean = false,
    val showDeleteItemConfirmation: Boolean = false,
    val selectedItem: ShoppingListItem? = null,
    val itemToDelete: ShoppingListItem? = null,
    val showCompletedItems: Boolean = true
)
