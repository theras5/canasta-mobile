package com.example.canasta.ui.screens.lists

import com.example.canasta.data.model.ShoppingList

data class ListsUiState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val searchQuery: String = "",
    val isSharedFilter: Boolean? = null,
    val isLoading: Boolean = false,
    val isFetching: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val selectedList: ShoppingList? = null,
    val showCreateModal: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val listToDelete: ShoppingList? = null
)
