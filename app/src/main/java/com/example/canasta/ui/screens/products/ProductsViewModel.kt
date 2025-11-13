package com.example.canasta.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() = runOnViewModelScope(
        { productRepository.getProducts(category = _uiState.value.selectedCategory, search = _uiState.value.searchQuery) },
        { state, products -> state.copy(products = products) }
    )

    fun loadCategories() = runOnViewModelScope(
        { productRepository.getCategories() },
        { state, categories -> state.copy(categories = categories) }
    )

    fun searchProducts(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadProducts()
    }

    fun filterByCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts()
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadProducts()
        loadCategories()
    }

    private fun <T> runOnViewModelScope(
        execute: suspend () -> T,
        updateState: (currentState: ProductsUiState, result: T) -> ProductsUiState
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
