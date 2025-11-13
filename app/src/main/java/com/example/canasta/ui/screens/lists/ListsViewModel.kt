package com.example.canasta.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.model.ShoppingList
import com.example.canasta.data.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListsViewModel(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListsUiState())
    val uiState: StateFlow<ListsUiState> = _uiState.asStateFlow()

    init {
        loadShoppingLists()
    }

    fun loadShoppingLists() = runOnViewModelScope(
        {
            shoppingListRepository.getShoppingLists(
                isShared = _uiState.value.isSharedFilter,
                search = _uiState.value.searchQuery
            )
        },
        { state, lists -> state.copy(shoppingLists = lists) }
    )

    fun searchLists(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadShoppingLists()
    }

    fun filterByShared(isShared: Boolean?) {
        _uiState.update { it.copy(isSharedFilter = isShared) }
        loadShoppingLists()
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadShoppingLists()
    }

    fun createShoppingList(name: String, description: String? = null, isShared: Boolean = false) = runOnViewModelScope(
        {
            val newList = ShoppingList(name = name, description = description, isShared = isShared)
            shoppingListRepository.createShoppingList(newList)
        },
        { state, createdList ->
            state.copy(
                shoppingLists = state.shoppingLists + createdList,
                showCreateModal = false
            )
        }
    )

    fun updateShoppingList(list: ShoppingList) = runOnViewModelScope(
        { shoppingListRepository.updateShoppingList(list) },
        { state, updatedList ->
            state.copy(
                shoppingLists = state.shoppingLists.map {
                    if (it.id == updatedList.id) updatedList else it
                }
            )
        }
    )

    fun deleteShoppingList(list: ShoppingList) = runOnViewModelScope(
        { shoppingListRepository.deleteShoppingList(list.id!!) },
        { state, _ ->
            state.copy(
                shoppingLists = state.shoppingLists.filter { it.id != list.id },
                showDeleteConfirmation = false,
                listToDelete = null
            )
        }
    )

    fun shareList(listId: Int, email: String) = runOnViewModelScope(
        { shoppingListRepository.shareList(listId, email) },
        { state, _ -> state }
    )

    fun unshareList(listId: Int) = runOnViewModelScope(
        { shoppingListRepository.unshareList(listId) },
        { state, _ -> state }
    )

    // UI State management
    fun showCreateModal() {
        _uiState.update { it.copy(showCreateModal = true) }
    }

    fun hideCreateModal() {
        _uiState.update { it.copy(showCreateModal = false) }
    }

    fun showDeleteConfirmation(list: ShoppingList) {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = true,
                listToDelete = list
            )
        }
    }

    fun hideDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                listToDelete = null
            )
        }
    }

    fun selectList(list: ShoppingList) {
        _uiState.update { it.copy(selectedList = list) }
    }

    private fun <T> runOnViewModelScope(
        execute: suspend () -> T,
        updateState: (currentState: ListsUiState, result: T) -> ListsUiState
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
