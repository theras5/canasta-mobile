package com.example.canasta.data

import com.example.canasta.data.api.ApiClient
import com.example.canasta.data.api.ShoppingListApi
import com.example.canasta.data.api.ShoppingListCreateDto
import com.example.canasta.data.api.ListItemApi
import com.example.canasta.ui.components.lists.ShoppingList
import com.example.canasta.ui.components.products.ListProduct
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Servicio singleton in-memory para gestionar listas de compras y sus ítems.
 *
 * La forma de uso está pensada para alinearse con la futura API descrita en swagger:
 *  - ShoppingList ~ definitions.ShoppingList
 *  - ListItem     ~ definitions.ListItem (aquí mapeado a ListProduct dentro de una lista)
 */
object ShoppingListService {

    private val api: ShoppingListApi = ApiClient.create(ShoppingListApi::class.java)
    private val itemsApi: ListItemApi = ApiClient.create(ListItemApi::class.java)

    // Estado in-memory de las listas
    private val _listsState = MutableStateFlow<List<ShoppingList>>(emptyList())
    val listsState: StateFlow<List<ShoppingList>> = _listsState.asStateFlow()

    // Map de productos por id de lista
    private val _productsByList = MutableStateFlow<Map<String, List<ListProduct>>>(emptyMap())
    val productsByList: StateFlow<Map<String, List<ListProduct>>> = _productsByList.asStateFlow()

    init {
        // Estado vacío; se llenará desde la API mediante refreshLists()
    }

    /**
     * Refresca las listas de compras desde la API (equivalente a GET /shopping-lists)
     */
    suspend fun refreshLists() {
        try {
            val remote = api.getShoppingLists()
            _listsState.value = remote.map { dto ->
                ShoppingList(
                    id = dto.id.toString(),
                    name = dto.name,
                    productCount = 0, // La API no devuelve conteo directo
                    icon = "\uD83D\uDCCB",
                    isFavorite = false
                )
            }
        } catch (e: Exception) {
            // En caso de error, dejamos el estado actual y logueamos
            println("Error al cargar listas desde API: ${e.message}")
        }
    }

    /**
     * Refresca los productos de una lista desde la API y actualiza el cache local.
     */
    suspend fun refreshProductsForList(listId: String) {
        try {
            val dtoItems = itemsApi.getItemsForList(listId.toLong())
            val mapped = dtoItems.map { dto ->
                val quantityPart = dto.quantity?.let { q ->
                    val unit = dto.unit ?: "unidades"
                    "${q.toInt()} $unit"
                } ?: ""
                ListProduct(
                    id = dto.id.toString(),
                    name = dto.product.name,
                    description = quantityPart,
                    isChecked = dto.purchased == true,
                    isPurchased = dto.purchased == true
                )
            }
            _productsByList.value = _productsByList.value.toMutableMap().apply {
                put(listId, mapped)
            }
        } catch (e: Exception) {
            println("Error al cargar items de la lista $listId desde API: ${e.message}")
        }
    }

    /**
     * Devuelve el flujo de listas actual (equivalente a GET /shopping-lists)
     */
    fun getShoppingLists(): StateFlow<List<ShoppingList>> = listsState

    /**
     * Devuelve los productos de una lista concreta (equivalente aproximado a GET /shopping-lists/{id}/items)
     */
    fun getProductsForList(listId: String): List<ListProduct> =
        productsByList.value[listId] ?: emptyList()

    /**
     * Crea una nueva lista (simula POST /shopping-lists)
     */
    suspend fun createList(name: String, icon: String?): ShoppingList {
        return try {
            val body = ShoppingListCreateDto(
                name = name,
                description = null,
                recurring = false,
                metadata = emptyMap()
            )
            val dto = api.createShoppingList(body)
            val newList = ShoppingList(
                id = dto.id.toString(),
                name = dto.name,
                productCount = 0,
                icon = icon ?: "\uD83D\uDCCB",
                isFavorite = false
            )
            _listsState.value = _listsState.value + newList
            newList
        } catch (e: Exception) {
            println("Error al crear lista en API: ${e.message}")
            // Fallback in-memory para no romper la UI
            val fallback = ShoppingList(
                id = UUID.randomUUID().toString(),
                name = name,
                productCount = 0,
                icon = icon ?: "\uD83D\uDCCB",
                isFavorite = false
            )
            _listsState.value = _listsState.value + fallback
            fallback
        }
    }

    /**
     * Actualiza los datos básicos de una lista (simula PUT /shopping-lists/{id})
     */
    suspend fun updateList(listId: String, newName: String? = null): ShoppingList? {
        delay(100)
        var updated: ShoppingList? = null
        _listsState.value = _listsState.value.map { list ->
            if (list.id == listId) {
                val changed = list.copy(name = newName ?: list.name)
                updated = changed
                changed
            } else list
        }
        return updated
    }

    /**
     * Elimina una lista (simula DELETE /shopping-lists/{id})
     */
    suspend fun deleteList(listId: String) {
        delay(100)
        _listsState.value = _listsState.value.filter { it.id != listId }
        _productsByList.value = _productsByList.value - listId
    }

    /**
     * Agrega o actualiza un producto dentro de una lista (simula POST/PUT de ListItem)
     */
    suspend fun upsertProduct(listId: String, product: ListProduct) {
        delay(50)
        val currentList = _productsByList.value[listId] ?: emptyList()
        val newList = if (currentList.any { it.id == product.id }) {
            currentList.map { if (it.id == product.id) product else it }
        } else {
            currentList + product
        }
        _productsByList.value = _productsByList.value.toMutableMap().apply {
            put(listId, newList)
        }

        // Actualizar contador de productos en la lista
        _listsState.value = _listsState.value.map { list ->
            if (list.id == listId) list.copy(productCount = newList.size) else list
        }
    }

    /**
     * Elimina un producto de una lista (simula DELETE /shopping-lists/{id}/items/{itemId})
     */
    suspend fun deleteProductFromList(listId: String, productId: String) {
        delay(50)
        val currentList = _productsByList.value[listId] ?: return
        val newList = currentList.filter { it.id != productId }
        _productsByList.value = _productsByList.value.toMutableMap().apply {
            put(listId, newList)
        }
        _listsState.value = _listsState.value.map { list ->
            if (list.id == listId) list.copy(productCount = newList.size) else list
        }
    }
}
