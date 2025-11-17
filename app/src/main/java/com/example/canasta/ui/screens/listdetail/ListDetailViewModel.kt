package com.example.canasta.ui.screens.listdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.ShoppingListService
import com.example.canasta.data.remote.ApiClient
import com.example.canasta.data.remote.models.ListItemUpdate
import com.example.canasta.data.remote.models.ShoppingListUpdate
import com.example.canasta.data.remote.models.TogglePurchasedRequest
import com.example.canasta.data.repository.CategoryRepository
import com.example.canasta.data.repository.ProductRepository
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.data.remote.models.Product
import com.example.canasta.ui.components.products.ListProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        val currentListId = _uiState.value.listId
        if (currentListId.isBlank()) return

        // Actualización optimista: agregar el producto inmediatamente al UI
        val newProduct = ListProduct(
            id = "temp_${product.id}", // ID temporal hasta que la API responda
            name = product.name,
            description = "1 unidades",
            isChecked = false,
            isPurchased = false
        )

        val currentProducts = _uiState.value.products
        _uiState.value = _uiState.value.copy(
            products = currentProducts + newProduct
        )

        // Luego hacer la llamada a la API en background
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Llamada real a API
                ShoppingListService.addProductToListApi(
                    listId = currentListId,
                    productId = product.id,
                    quantity = 1.0,
                    unit = "unidades"
                )
                // Refrescar para obtener el ID real y datos actualizados del servidor
                val refreshed = ShoppingListService.getProductsForList(currentListId)
                _uiState.value = _uiState.value.copy(products = refreshed)
            } catch (e: Exception) {
                // Si falla, remover el producto temporal y mostrar error
                _uiState.value = _uiState.value.copy(
                    products = currentProducts, // Revertir a la lista anterior
                    error = "Error al agregar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Carga los datos de una lista específica por ID
     */
    fun loadList(listId: String, listName: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

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
     * Guarda los cambios usando la API real
     */
    private fun saveChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val current = _uiState.value
                val listId = current.listId.toLongOrNull()

                if (listId != null) {
                    // Actualizar nombre de la lista en la API
                    val updateRequest = ShoppingListUpdate(name = current.listName)
                    val response = ApiClient.shoppingListService.updateShoppingList(listId, updateRequest)

                    if (response.isSuccessful) {
                        // También actualizar en el servicio local
                        ShoppingListService.updateList(current.listId, current.listName)

                        originalListName = current.listName
                        originalProducts = current.products.map { it.copy() }

                        _uiState.value = current.copy(
                            screenMode = ScreenMode.VIEW,
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al guardar: ${response.code()}"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "ID de lista inválido"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    /**
     * Actualiza la cantidad/descripción de un producto usando la API real
     */
    fun updateProductQuantity(productId: String, newQuantity: String) {
        // Primero actualizar UI optimistamente
        val updatedProducts = _uiState.value.products.map { product ->
            if (product.id == productId) {
                product.copy(description = newQuantity)
            } else {
                product
            }
        }
        _uiState.value = _uiState.value.copy(products = updatedProducts)

        // Luego actualizar en la API
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listId = _uiState.value.listId.toLongOrNull()
                val itemId = productId.toLongOrNull()

                if (listId != null && itemId != null) {
                    // Parsear la descripción para extraer cantidad y unidad
                    // Formato esperado: "X unidades" o solo un número
                    val parts = newQuantity.trim().split(" ")
                    val quantity = parts.firstOrNull()?.toDoubleOrNull() ?: 1.0
                    val unit = if (parts.size > 1) parts.drop(1).joinToString(" ") else "unidades"

                    val updateRequest = ListItemUpdate(
                        quantity = quantity,
                        unit = unit
                    )

                    val response = ApiClient.shoppingListService.updateListItem(listId, itemId, updateRequest)
                    if (!response.isSuccessful) {
                        _uiState.value = _uiState.value.copy(
                            error = "Error al actualizar producto: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Elimina un producto de la lista usando la API real
     */
    fun deleteProduct(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listId = _uiState.value.listId.toLongOrNull()
                val itemId = productId.toLongOrNull()

                if (listId != null && itemId != null) {
                    val response = ApiClient.shoppingListService.deleteListItem(listId, itemId)
                    if (response.isSuccessful) {
                        // Actualizar UI optimistamente
                        val updatedProducts = _uiState.value.products.filter { it.id != productId }
                        _uiState.value = _uiState.value.copy(products = updatedProducts)

                        // También eliminar del servicio local
                        ShoppingListService.deleteProductFromList(_uiState.value.listId, productId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "Error al eliminar producto: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Elimina la lista completa usando la API real
     */
    fun deleteList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listId = _uiState.value.listId.toLongOrNull()
                if (listId != null) {
                    val response = ApiClient.shoppingListService.deleteShoppingList(listId)
                    if (response.isSuccessful) {
                        // También eliminar del servicio local
                        ShoppingListService.deleteList(_uiState.value.listId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "Error al eliminar lista: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar lista: ${e.message}"
                )
            }
        }
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
