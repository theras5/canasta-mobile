package com.example.canasta.ui.screens.listdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.model.ShoppingListItem
import com.example.canasta.data.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListDetailViewModel(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState: StateFlow<ListDetailUiState> = _uiState.asStateFlow()

    fun loadListDetail(listId: Int) = runOnViewModelScope(
        { shoppingListRepository.getShoppingList(listId) },
        { state, list ->
            state.copy(shoppingList = list)
        }
    )

    fun loadListItems(listId: Int) = runOnViewModelScope(
        { shoppingListRepository.getListItems(listId) },
        { state, items ->
            val completed = items.filter { it.isCompleted == true }
            val pending = items.filter { it.isCompleted != true }
            state.copy(
                items = items,
                completedItems = completed,
                pendingItems = pending
            )
        }
    )

    fun addItemToList(
        listId: Int,
        productId: Int?,
        productName: String,
        quantity: Int = 1,
        notes: String? = null
    ) = runOnViewModelScope(
        {
            val newItem = ShoppingListItem(
                null, // id
                null, // shoppingListId
                productId,
                productName,
                quantity,
                false, // isCompleted
                notes,
                null, // createdAt
                null  // updatedAt
            )
            shoppingListRepository.addItemToList(listId, newItem)
        },
        { state, createdItem ->
            val newItems = state.items + createdItem
            val newPending = state.pendingItems + createdItem
            state.copy(
                items = newItems,
                pendingItems = newPending,
                showAddItemModal = false
            )
        }
    )

    fun updateListItem(listId: Int, item: ShoppingListItem) = runOnViewModelScope(
        { shoppingListRepository.updateListItem(listId, item) },
        { state, updatedItem ->
            val newItems = state.items.map { if (it.id == updatedItem.id) updatedItem else it }
            val completed = newItems.filter { it.isCompleted == true }
            val pending = newItems.filter { it.isCompleted != true }
            state.copy(
                items = newItems,
                completedItems = completed,
                pendingItems = pending,
                showEditItemModal = false,
                selectedItem = null
            )
        }
    )

    fun toggleItemCompleted(listId: Int, itemId: Int) = runOnViewModelScope(
        { shoppingListRepository.toggleItemCompleted(listId, itemId) },
        { state, updatedItem ->
            val newItems = state.items.map { if (it.id == updatedItem.id) updatedItem else it }
            val completed = newItems.filter { it.isCompleted == true }
            val pending = newItems.filter { it.isCompleted != true }
            state.copy(
                items = newItems,
                completedItems = completed,
                pendingItems = pending
            )
        }
    )

    fun deleteListItem(listId: Int, itemId: Int) = runOnViewModelScope(
        { shoppingListRepository.removeItemFromList(listId, itemId) },
        { state, _ ->
            val newItems = state.items.filter { it.id != itemId }
            val completed = newItems.filter { it.isCompleted == true }
            val pending = newItems.filter { it.isCompleted != true }
            state.copy(
                items = newItems,
                completedItems = completed,
                pendingItems = pending,
                showDeleteItemConfirmation = false,
                itemToDelete = null
            )
        }
    )

    fun refresh(listId: Int) {
        _uiState.update { it.copy(isRefreshing = true) }
        loadListDetail(listId)
        loadListItems(listId)
    }

    // UI State management
    fun showAddItemModal() {
        _uiState.update { it.copy(showAddItemModal = true) }
    }

    fun hideAddItemModal() {
        _uiState.update { it.copy(showAddItemModal = false) }
    }

    fun showEditItemModal(item: ShoppingListItem) {
        _uiState.update {
            it.copy(
                showEditItemModal = true,
                selectedItem = item
            )
        }
    }

    fun hideEditItemModal() {
        _uiState.update {
            it.copy(
                showEditItemModal = false,
                selectedItem = null
            )
        }
    }

    fun showDeleteItemConfirmation(item: ShoppingListItem) {
        _uiState.update {
            it.copy(
                showDeleteItemConfirmation = true,
                itemToDelete = item
            )
        }
    }

    fun hideDeleteItemConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteItemConfirmation = false,
                itemToDelete = null
            )
        }
    }

    fun toggleShowCompletedItems() {
        _uiState.update { it.copy(showCompletedItems = !it.showCompletedItems) }
    }

    private fun <T> runOnViewModelScope(
        execute: suspend () -> T,
        updateState: (currentState: ListDetailUiState, result: T) -> ListDetailUiState
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isFetching = true, error = null) }
        runCatching { execute() }
            .onSuccess { result ->
                _uiState.update {
                    updateState(it, result).copy(
                        isFetching = false,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(
                        isFetching = false,
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
    }
}
