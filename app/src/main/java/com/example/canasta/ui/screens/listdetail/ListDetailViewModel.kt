package com.example.canasta.ui.screens.listdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.R
import com.example.canasta.data.ShoppingListService
import com.example.canasta.data.remote.ApiClient
import com.example.canasta.data.remote.models.ListItemUpdate
import com.example.canasta.data.remote.models.ShoppingListUpdate
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
    val successMessage: String? = null,
    val availableProducts: List<Product> = emptyList(),
    val availableCategories: List<GetCategory> = emptyList(),
    val sharedUsers: List<com.example.canasta.data.remote.models.SharedUser> = emptyList(),
    val shareError: String? = null,
    val isSharing: Boolean = false,
    val isOwner: Boolean = true, // Por defecto true para mostrar todos los botones
    val currentUserId: Long? = null,
    val shouldDeleteAndNavigateBack: Boolean = false // Indica que la lista debe ser eliminada y volver atrás
)

/**
 * ViewModel para la pantalla de detalle de lista
 * Gestiona el estado de UI y la lógica de negocio
 */
class ListDetailViewModel(application: Application) : AndroidViewModel(application) {

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

        // Observar cambios en los productos desde el servicio
        observeProductChanges()
    }

    /**
     * Observa cambios en los productos del servicio y actualiza la UI automáticamente
     */
    private fun observeProductChanges() {
        viewModelScope.launch {
            ShoppingListService.productsByList.collect { productsByList ->
                val currentListId = _uiState.value.listId
                if (currentListId.isNotBlank()) {
                    val apiProducts = productsByList[currentListId] ?: emptyList()

                    // Actualizar la UI con los productos de la API
                    _uiState.value = _uiState.value.copy(
                        products = apiProducts
                    )
                }
            }
        }
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
                    error = getApplication<Application>().getString(R.string.error_loading_products_list, e.message ?: "")
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

        // Enviar a la API
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ShoppingListService.addProductToListApi(
                    listId = currentListId,
                    productId = product.id,
                    quantity = 1.0,
                    unit = "unidades"
                )
                // El observer actualizará la UI automáticamente
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al agregar ${product.name}"
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
            try {
                // Obtener el usuario currente
                val currentUser = try {
                    ApiClient.userService.getUserProfile()
                } catch (e: Exception) {
                    null
                }

                // Obtener detalles completos de la lista (incluye owner)
                val listDetails = try {
                    val response = ApiClient.shoppingListService.getShoppingListById(listId.toLong())
                    if (response.isSuccessful) response.body() else null
                } catch (e: Exception) {
                    null
                }

                // Determinar si el usuario currente es el owner
                val isOwner = if (currentUser != null && listDetails?.owner != null) {
                    currentUser.id == listDetails.owner.id
                } else {
                    true // Por defecto true si no podemos determinar
                }

                // Traer productos reales de la API a través del servicio
                ShoppingListService.refreshProductsForList(listId)

                // Obtener listas actuales del servicio y buscar la que corresponde
                val lists = ShoppingListService.listsState.value
                val list = lists.firstOrNull { it.id == listId }
                val effectiveName = listDetails?.name ?: list?.name ?: listName
                val products = ShoppingListService.getProductsForList(listId)

                _uiState.value = _uiState.value.copy(
                    listId = listId,
                    listName = effectiveName,
                    products = products,
                    isLoading = false,
                    isOwner = isOwner,
                    currentUserId = currentUser?.id
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = getApplication<Application>().getString(R.string.error_loading_list, e.message ?: "")
                )
            }
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
                            error = getApplication<Application>().getString(R.string.error_saving_list, response.code().toString())
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = getApplication<Application>().getString(R.string.error_invalid_list_id)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = getApplication<Application>().getString(R.string.error_saving_list, e.message ?: "")
                )
            }
        }
    }

    /**
     * Actualiza la cantidad y unidad de un producto usando la API real
     */
    fun updateProductQuantity(productId: String, newQuantity: Double, newUnit: String) {
        // Construimos una descripción amigable para mostrar en la tarjeta ("1 unidades", "2 kg", etc.)
        val descriptionText = if (newUnit.isBlank()) {
            newQuantity.toString()
        } else {
            "${newQuantity} ${newUnit}"
        }

        // Primero actualizar UI optimistamente
        val updatedProducts = _uiState.value.products.map { product ->
            if (product.id == productId) {
                product.copy(description = descriptionText)
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
                    val updateRequest = ListItemUpdate(
                        quantity = newQuantity,
                        unit = newUnit,
                        metadata = null
                    )

                    val response = ApiClient.shoppingListService.updateListItem(listId, itemId, updateRequest)
                    if (!response.isSuccessful) {
                        _uiState.value = _uiState.value.copy(
                            error = getApplication<Application>().getString(R.string.error_updating_product_list, response.code().toString())
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = getApplication<Application>().getString(R.string.error_updating_product_list, e.message ?: "")
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
                        _uiState.value = _uiState.value.copy(
                            products = _uiState.value.products.filter { it.id != productId },
                            successMessage = getApplication<Application>().getString(R.string.product_deleted_success_list)
                        )

                        // También eliminar del servicio local
                        ShoppingListService.deleteProductFromList(_uiState.value.listId, productId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = getApplication<Application>().getString(R.string.error_deleting_product_list, response.code().toString())
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = getApplication<Application>().getString(R.string.error_deleting_product_list, e.message ?: "")
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
                            error = getApplication<Application>().getString(R.string.error_deleting_list_detail, response.code().toString())
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = getApplication<Application>().getString(R.string.error_deleting_list_detail, e.message ?: "")
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

                // Hacer el toggle con el filtro de categoría actual
                val categoryId = _uiState.value.selectedCategory?.id
                ShoppingListService.toggleItemPurchasedApi(currentListId, productId, newPurchased, categoryId)

                // La UI se actualizará automáticamente gracias al observer

                // Para verificar si todos están comprados, necesitamos consultar TODOS los productos sin filtro
                val allProducts = ShoppingListService.getAllProductsFromApi(currentListId)

                println("DEBUG: Lista $currentListId tiene ${allProducts.size} productos en total")
                println("DEBUG: Productos comprados: ${allProducts.count { it.purchased == true }}")

                // Verificar si todos los productos (sin filtro) están comprados
                if (allProducts.isNotEmpty() && allProducts.all { it.purchased == true }) {
                    println("DEBUG: Todos los productos están comprados, eliminando lista automáticamente")
                    // Todos los productos están comprados, eliminar la lista
                    deleteListAutomatically()
                }
            } catch (e: Exception) {
                println("ERROR en toggleProductCheck: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    error = getApplication<Application>().getString(R.string.error_updating_product_list, e.message ?: "")
                )
            }
        }
    }

    /**
     * Elimina la lista automáticamente cuando todos los productos están comprados
     */
    private suspend fun deleteListAutomatically() {
        try {
            println("DEBUG: Iniciando eliminación automática de lista")
            val listId = _uiState.value.listId.toLongOrNull()
            println("DEBUG: ListId para eliminar: $listId")
            if (listId != null) {
                // Eliminar la lista en el backend
                println("DEBUG: Llamando a deleteShoppingList en API")
                val response = ApiClient.shoppingListService.deleteShoppingList(listId)
                println("DEBUG: Respuesta de API: isSuccessful=${response.isSuccessful}, code=${response.code()}")
                if (response.isSuccessful) {
                    // Actualizar el servicio local
                    println("DEBUG: Actualizando servicio local")
                    ShoppingListService.deleteList(_uiState.value.listId)
                    // Refrescar las listas
                    println("DEBUG: Refrescando listas")
                    ShoppingListService.refreshLists()
                    // Indicar que se debe navegar de vuelta
                    println("DEBUG: Marcando shouldDeleteAndNavigateBack = true")
                    _uiState.value = _uiState.value.copy(
                        shouldDeleteAndNavigateBack = true,
                        successMessage = "Lista completada y eliminada"
                    )
                    println("DEBUG: Estado actualizado, shouldDeleteAndNavigateBack=${_uiState.value.shouldDeleteAndNavigateBack}")
                } else {
                    println("ERROR: La respuesta no fue exitosa: ${response.code()} - ${response.message()}")
                }
            } else {
                println("ERROR: listId es null, no se puede eliminar")
            }
        } catch (e: Exception) {
            println("ERROR en deleteListAutomatically: ${e.message}")
            e.printStackTrace()
            _uiState.value = _uiState.value.copy(
                error = "Error al eliminar lista completada: ${e.message}"
            )
        }
    }

    /**
     * Resetea el flag de eliminación automática
     */
    fun resetDeleteAndNavigateBack() {
        _uiState.value = _uiState.value.copy(shouldDeleteAndNavigateBack = false)
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
                // La UI se actualizará automáticamente gracias al observer
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

    /**
     * Carga los usuarios con los que está compartida la lista
     */
    fun loadSharedUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listId = _uiState.value.listId.toLongOrNull()
                if (listId != null) {
                    val response = ApiClient.shoppingListService.getSharedUsers(listId)
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            sharedUsers = users,
                            shareError = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            shareError = getApplication<Application>().getString(R.string.error_loading_users, response.code().toString())
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    shareError = getApplication<Application>().getString(R.string.error_loading_users, e.message ?: "")
                )
            }
        }
    }

    /**
     * Comparte la lista con un usuario por email
     */
    fun shareListWithEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isSharing = true, shareError = null)
            try {
                val listId = _uiState.value.listId.toLongOrNull()
                if (listId != null) {
                    val request = com.example.canasta.data.remote.models.ShareListRequest(email = email)
                    val response = ApiClient.shoppingListService.shareShoppingList(listId, request)

                    if (response.isSuccessful) {
                        // Recargar la lista de usuarios compartidos
                        loadSharedUsers()
                        _uiState.value = _uiState.value.copy(
                            isSharing = false,
                            shareError = null
                        )
                    } else {
                        val errorMsg = when (response.code()) {
                            404 -> getApplication<Application>().getString(R.string.user_not_found)
                            409 -> getApplication<Application>().getString(R.string.list_already_shared)
                            400 -> getApplication<Application>().getString(R.string.cannot_share_with_yourself)
                            else -> getApplication<Application>().getString(R.string.error_sharing_list, response.code().toString())
                        }
                        _uiState.value = _uiState.value.copy(
                            isSharing = false,
                            shareError = errorMsg
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        shareError = getApplication<Application>().getString(R.string.error_invalid_list_id_share)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSharing = false,
                    shareError = getApplication<Application>().getString(R.string.error_sharing_list, e.message ?: "")
                )
            }
        }
    }

    /**
     * Revoca el acceso de un usuario a la lista
     */
    fun revokeShareAccess(user: com.example.canasta.data.remote.models.SharedUser) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listId = _uiState.value.listId.toLongOrNull()
                if (listId != null) {
                    val response = ApiClient.shoppingListService.revokeShareShoppingList(listId, user.id)

                    if (response.isSuccessful) {
                        // Actualizar la lista eliminando el usuario
                        val updatedUsers = _uiState.value.sharedUsers.filter { it.id != user.id }
                        _uiState.value = _uiState.value.copy(
                            sharedUsers = updatedUsers,
                            shareError = null
                        )
                        loadSharedUsers() // Recargar la lista de usuarios
                    } else {
                        _uiState.value = _uiState.value.copy(
                            shareError = getApplication<Application>().getString(R.string.error_revoking_access, response.code().toString())
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    shareError = getApplication<Application>().getString(R.string.error_revoking_access, e.message ?: "")
                )
            }
        }
    }

    /**
     * Limpia el error de compartir
     */
    fun clearShareError() {
        _uiState.value = _uiState.value.copy(shareError = null)
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
