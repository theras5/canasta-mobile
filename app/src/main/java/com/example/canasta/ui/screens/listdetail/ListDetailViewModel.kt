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
     * Agrega un producto a la lista actual
     * Los productos nuevos se agregan al inicio de la lista
     */
    fun addProductToList(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("DEBUG ViewModel: Agregando producto '${product.name}' a la lista")

                // Crear el ListProduct localmente
                val listProduct = ListProduct(
                    id = UUID.randomUUID().toString(),
                    name = product.name,
                    description = "1 unidades",
                    isChecked = false,
                    isPurchased = false
                )

                println("DEBUG ViewModel: ListProduct creado con nombre '${listProduct.name}'")

                // Agregar al servicio local
                ShoppingListService.upsertProduct(_uiState.value.listId, listProduct)

                // Actualizar UI - agregar al INICIO de la lista
                val currentProducts = _uiState.value.products
                val updatedProducts = listOf(listProduct) + currentProducts
                _uiState.value = _uiState.value.copy(products = updatedProducts)

                println("DEBUG ViewModel: Estado actualizado. Productos en lista: ${updatedProducts.size}")
                println("DEBUG ViewModel: Nombres en lista: ${updatedProducts.map { it.name }}")

            } catch (e: Exception) {
                println("ERROR ViewModel: Al agregar producto: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    error = "Error al agregar producto: ${e.message}"
                )
            }
        }
    }

    /**
     * Carga los datos de una lista específica por ID
     * En producción, esto haría una llamada a Room o API
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
     * Marca/desmarca un producto como completado
     */
    fun toggleProductCheck(productId: String) {
        val updatedProducts = _uiState.value.products.map { product ->
            if (product.id == productId) {
                product.copy(isChecked = !product.isChecked)
            } else {
                product
            }
        }
        _uiState.value = _uiState.value.copy(products = updatedProducts)
    }

    /**
     * Cambia la categoría seleccionada
     */
    fun selectCategory(category: GetCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    /**
     * Valida que el nombre de la lista no esté vacío
     */
    fun validateListName(): Boolean {
        val name = _uiState.value.listName.trim()
        return name.isNotEmpty()
    }
}
