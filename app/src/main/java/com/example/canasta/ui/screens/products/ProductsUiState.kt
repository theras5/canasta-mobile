package com.example.canasta.ui.screens.products

import com.example.canasta.data.model.Product

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isFetching: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)
