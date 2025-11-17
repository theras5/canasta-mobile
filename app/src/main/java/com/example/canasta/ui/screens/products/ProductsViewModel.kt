package com.example.canasta.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.R
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
    data class Error(val messageResId: Int) : ProductsUiState()
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

    private val _errorMessageResId = MutableStateFlow<Int?>(null)
    val errorMessageResId: StateFlow<Int?> = _errorMessageResId.asStateFlow()

    private val _successMessageResId = MutableStateFlow<Int?>(null)
    val successMessageResId: StateFlow<Int?> = _successMessageResId.asStateFlow()

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
            _errorMessageResId.value = null
            _uiState.value = ProductsUiState.Loading

            repository.getProducts(name, categoryId, pantryId).fold(
                onSuccess = { products ->
                    _products.value = products
                    _uiState.value = ProductsUiState.Success(products)
                    _isLoading.value = false
                },
                onFailure = { error ->
                    val messageResId = R.string.error_loading_products
                    _errorMessageResId.value = messageResId
                    _uiState.value = ProductsUiState.Error(messageResId)
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
            _errorMessageResId.value = null
            _successMessageResId.value = null

            repository.createProduct(name, categoryId, metadata).fold(
                onSuccess = {
                    _successMessageResId.value = R.string.product_created_success
                    loadProducts() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessageResId.value = R.string.error_creating_product
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
            _errorMessageResId.value = null
            _successMessageResId.value = null

            repository.updateProduct(id, name, categoryId, metadata).fold(
                onSuccess = {
                    _successMessageResId.value = R.string.product_updated_success
                    loadProducts() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessageResId.value = R.string.error_updating_product
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
            _errorMessageResId.value = null
            _successMessageResId.value = null

            repository.deleteProduct(id).fold(
                onSuccess = {
                    _successMessageResId.value = R.string.product_deleted_success
                    loadProducts() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessageResId.value = R.string.error_deleting_product
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessageResId.value = null
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccess() {
        _successMessageResId.value = null
    }
}
