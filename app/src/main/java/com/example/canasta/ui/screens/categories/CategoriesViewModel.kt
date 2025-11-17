package com.example.canasta.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.R
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles para la pantalla de categorías
 */
sealed class CategoriesUiState {
    object Loading : CategoriesUiState()
    data class Success(val categories: List<GetCategory>) : CategoriesUiState()
    data class Error(val messageResId: Int) : CategoriesUiState()
}

/**
 * ViewModel para la pantalla de categorías
 */
class CategoriesViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _uiState = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<GetCategory>>(emptyList())
    val categories: StateFlow<List<GetCategory>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessageResId = MutableStateFlow<Int?>(null)
    val errorMessageResId: StateFlow<Int?> = _errorMessageResId.asStateFlow()

    // Flujo para mensajes de éxito usando resource ID
    private val _successMessageResId = MutableStateFlow<Int?>(null)
    val successMessageResId: StateFlow<Int?> = _successMessageResId.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Carga todas las categorías
     */
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessageResId.value = null
            _uiState.value = CategoriesUiState.Loading

            repository.getCategories().fold(
                onSuccess = { categories ->
                    _categories.value = categories
                    _uiState.value = CategoriesUiState.Success(categories)
                    _isLoading.value = false
                },
                onFailure = { error ->
                    val messageResId = R.string.error_loading_categories
                    _errorMessageResId.value = messageResId
                    _uiState.value = CategoriesUiState.Error(messageResId)
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Crea una nueva categoría
     */
    fun createCategory(name: String, metadata: Map<String, String>? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessageResId.value = null
            _successMessageResId.value = null

            repository.createCategory(name, metadata).fold(
                onSuccess = {
                    _successMessageResId.value = R.string.category_created_success
                    loadCategories() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessageResId.value = R.string.error_creating_category
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Actualiza una categoría existente
     */
    fun updateCategory(
        id: Long,
        name: String? = null,
        metadata: Map<String, String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessageResId.value = null
            _successMessageResId.value = null

            repository.updateCategory(id, name, metadata).fold(
                onSuccess = {
                    _successMessageResId.value = R.string.category_updated_success
                    loadCategories() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessageResId.value = R.string.error_updating_category
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Elimina una categoría
     */
    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessageResId.value = null
            _successMessageResId.value = null

            repository.deleteCategory(id).fold(
                onSuccess = {
                    _successMessageResId.value = R.string.category_deleted_success
                    loadCategories() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessageResId.value = R.string.error_deleting_category
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
