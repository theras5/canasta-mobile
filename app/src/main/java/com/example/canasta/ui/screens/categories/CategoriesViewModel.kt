package com.example.canasta.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    data class Error(val message: String) : CategoriesUiState()
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Flujo para mensajes de éxito (faltaba la declaración)
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Carga todas las categorías
     */
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _uiState.value = CategoriesUiState.Loading

            repository.getCategories().fold(
                onSuccess = { categories ->
                    _categories.value = categories
                    _uiState.value = CategoriesUiState.Success(categories)
                    _isLoading.value = false
                },
                onFailure = { error ->
                    val message = error.message ?: "Error al cargar categorías"
                    _errorMessage.value = message
                    _uiState.value = CategoriesUiState.Error(message)
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
            _errorMessage.value = null
            _successMessage.value = null

            repository.createCategory(name, metadata).fold(
                onSuccess = {
                    _successMessage.value = "Categoría creada exitosamente"
                    loadCategories() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al crear categoría"
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
            _errorMessage.value = null
            _successMessage.value = null

            repository.updateCategory(id, name, metadata).fold(
                onSuccess = {
                    _successMessage.value = "Categoría actualizada exitosamente"
                    loadCategories() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al actualizar categoría"
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
            _errorMessage.value = null
            _successMessage.value = null

            repository.deleteCategory(id).fold(
                onSuccess = {
                    _successMessage.value = "Categoría eliminada exitosamente"
                    loadCategories() // Recargar la lista
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al eliminar categoría"
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

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccess() {
        _successMessage.value = null
    }
}
