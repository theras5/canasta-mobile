package com.example.canasta.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.remote.models.Product
import com.example.canasta.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles para la pantalla de productos
 */
sealed class ProductsUiState {
    object Loading : ProductsUiState()
    data class Success(val products: List<Product>) : ProductsUiState()
    data class Error(val message: String) : ProductsUiState()
}

/**
 * ViewModel para la pantalla de productos
 */
class ProductsViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Carga todos los productos
     */
    fun loadProducts(
        name: String? = null,
        categoryId: Long? = null,
        pantryId: Long? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _uiState.value = ProductsUiState.Loading

            repository.getProducts(name, categoryId, pantryId).fold(
                onSuccess = { products ->
                    _products.value = products
                    _uiState.value = ProductsUiState.Success(products)
                    _isLoading.value = false
                },
                onFailure = { error ->
                    val message = error.message ?: "Error al cargar productos"
                    _errorMessage.value = message
                    _uiState.value = ProductsUiState.Error(message)
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Crea un nuevo producto
     */
    fun createProduct(
        name: String,
        categoryId: Long?,
        metadata: Map<String, String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.createProduct(name, categoryId, metadata).fold(
                onSuccess = {
                    loadProducts() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al crear producto"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Actualiza un producto existente
     */
    fun updateProduct(
        id: Long,
        name: String? = null,
        categoryId: Long? = null,
        metadata: Map<String, String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.updateProduct(id, name, categoryId, metadata).fold(
                onSuccess = {
                    loadProducts() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al actualizar producto"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Elimina un producto
     */
    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.deleteProduct(id).fold(
                onSuccess = {
                    loadProducts() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al eliminar producto"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
