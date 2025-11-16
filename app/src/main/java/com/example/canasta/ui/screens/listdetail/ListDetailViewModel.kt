package com.example.canasta.ui.screens.listdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.ShoppingListService
import com.example.canasta.data.repository.CategoryRepository
import com.example.canasta.data.repository.ProductRepository
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.data.remote.models.Product
import com.example.canasta.ui.components.products.ListProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Enum que representa los modos de la pantalla de detalle
 */
enum class ScreenMode {
    VIEW,   // Modo de visualización (solo lectura)
    EDIT    // Modo de edición (permite modificar)
}

/**
 * Estado de la UI para ListDetailScreen
 */
data class ListDetailUiState(
    val listId: String = "",
    val listName: String = "",
    val products: List<ListProduct> = emptyList(),
    val selectedCategory: GetCategory? = null, // null = "Todos"
    val categories: List<GetCategory> = emptyList(), // Categorías reales de la API
    val screenMode: ScreenMode = ScreenMode.VIEW,
    val isLoading: Boolean = false,
    val error: String? = null,
    val availableProducts: List<Product> = emptyList(),
    val availableCategories: List<GetCategory> = emptyList()
)

/**
 * ViewModel para la pantalla de detalle de lista
 * Gestiona el estado de UI y la lógica de negocio
 */
class ListDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState: StateFlow<ListDetailUiState> = _uiState.asStateFlow()

    private val productRepository = ProductRepository()
    private val categoryRepository = CategoryRepository()

    // Estado temporal para edición (mantiene una copia de los datos originales)
    private var originalListName: String = ""
    private var originalProducts: List<ListProduct> = emptyList()

    init {
        // Cargar productos y categorías disponibles
        loadAvailableProductsAndCategories()
    }

    /**
     * Carga productos y categorías desde la API para el bottom sheet
     */
    private fun loadAvailableProductsAndCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Cargar productos
                val productsResult = productRepository.getProducts()
                productsResult.onSuccess { products ->
                    _uiState.value = _uiState.value.copy(availableProducts = products)
                }

                // Cargar categorías
                val categoriesResult = categoryRepository.getCategories()
                categoriesResult.onSuccess { categoriesList ->
                    _uiState.value = _uiState.value.copy(
                        availableCategories = categoriesList,
                        categories = categoriesList // También para los chips de filtro
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar productos: ${e.message}"
                )
            }
        }
    }

    /**
     * Agrega un producto a la lista actual usando la API real
     */
    fun addProductToList(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentListId = _uiState.value.listId
                if (currentListId.isBlank()) return@launch
                // Llamada real a API
                ShoppingListService.addProductToListApi(
                    listId = currentListId,
                    productId = product.id,
                    quantity = 1.0,
                    unit = "unidades"
                )
                // Luego de refrescar en el servicio, actualizamos productos en UI
                val refreshed = ShoppingListService.getProductsForList(currentListId)
                _uiState.value = _uiState.value.copy(products = refreshed)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al agregar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Carga los datos de una lista específica por ID
     */
    fun loadList(listId: String, listName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Traer productos reales de la API a través del servicio
            ShoppingListService.refreshProductsForList(listId)

            // Obtener listas actuales del servicio y buscar la que corresponde
            val lists = ShoppingListService.listsState.value
            val list = lists.firstOrNull { it.id == listId }
            val effectiveName = list?.name ?: listName
            val products = ShoppingListService.getProductsForList(listId)

            _uiState.value = _uiState.value.copy(
                listId = listId,
                listName = effectiveName,
                products = products,
                isLoading = false
            )
        }
    }

    /**
     * Cambia el modo entre VIEW y EDIT
     * Al entrar a modo edición, guarda una copia de los datos originales
     */
    fun toggleEditMode() {
        val currentState = _uiState.value

        if (currentState.screenMode == ScreenMode.VIEW) {
            // Entrando al modo edición - guardar estado original
            originalListName = currentState.listName
            originalProducts = currentState.products.map { it.copy() }

            _uiState.value = currentState.copy(screenMode = ScreenMode.EDIT)
        } else {
            // Saliendo del modo edición - guardar cambios
            saveChanges()
        }
    }

    /**
     * Cancela la edición y restaura los valores originales
     */
    fun cancelEdit() {
        _uiState.value = _uiState.value.copy(
            listName = originalListName,
            products = originalProducts,
            screenMode = ScreenMode.VIEW
        )
    }

    /**
     * Actualiza el nombre de la lista
     */
    fun updateListName(newName: String) {
        _uiState.value = _uiState.value.copy(listName = newName)
    }

    /**
     * Guarda los cambios en la base de datos
     * En producción, aquí harías la persistencia en Room
     */
    private fun saveChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Actualizar nombre de la lista en el servicio
                val current = _uiState.value
                ShoppingListService.updateList(current.listId, current.listName)

                // Persistir productos actuales en el servicio
                current.products.forEach { product ->
                    ShoppingListService.upsertProduct(current.listId, product)
                }

                originalListName = current.listName
                originalProducts = current.products.map { it.copy() }

                _uiState.value = current.copy(
                    screenMode = ScreenMode.VIEW,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    /**
     * Actualiza la cantidad/descripción de un producto
     */
    fun updateProductQuantity(productId: String, newQuantity: String) {
        val updatedProducts = _uiState.value.products.map { product ->
            if (product.id == productId) {
                product.copy(description = newQuantity)
            } else {
                product
            }
        }
        _uiState.value = _uiState.value.copy(products = updatedProducts)
    }

    /**
     * Elimina un producto de la lista
     */
    fun deleteProduct(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ShoppingListService.deleteProductFromList(_uiState.value.listId, productId)
        }
        val updatedProducts = _uiState.value.products.filter { it.id != productId }
        _uiState.value = _uiState.value.copy(products = updatedProducts)
    }

    /**
     * Marca/desmarca un producto como completado en backend
     */
    fun toggleProductCheck(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentListId = _uiState.value.listId
                if (currentListId.isBlank()) return@launch
                val current = _uiState.value.products.firstOrNull { it.id == productId }
                val newPurchased = !(current?.isPurchased ?: false)
                ShoppingListService.toggleItemPurchasedApi(currentListId, productId, newPurchased)
                val refreshed = ShoppingListService.getProductsForList(currentListId)
                _uiState.value = _uiState.value.copy(products = refreshed)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Cambia la categoría seleccionada: refresca items desde API usando category_id y actualiza UI
     */
    fun selectCategory(category: GetCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        val currentListId = _uiState.value.listId
        if (currentListId.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                // Llamar a API con filtro por categoría
                val categoryId = category?.id
                ShoppingListService.refreshProductsForList(currentListId, categoryId)
                // Actualizar productos en UI state tras la carga
                val filtered = ShoppingListService.getProductsForList(currentListId)
                _uiState.value = _uiState.value.copy(products = filtered)
            }
        }
    }

    /**
     * Valida que el nombre de la lista no esté vacío
     */
    fun validateListName(): Boolean {
        val name = _uiState.value.listName.trim()
        return name.isNotEmpty()
    }
}
