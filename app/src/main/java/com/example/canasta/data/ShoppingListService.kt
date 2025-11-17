package com.example.canasta.data

import com.example.canasta.data.remote.ApiClient
import com.example.canasta.data.api.ShoppingListApi
import com.example.canasta.data.api.ShoppingListCreateDto
import com.example.canasta.data.api.ListItemApi
import com.example.canasta.ui.components.lists.ShoppingList
import com.example.canasta.ui.components.products.ListProduct
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import retrofit2.HttpException
import com.example.canasta.data.api.ListItemCreateDto
import com.example.canasta.data.api.ProductRef
import com.example.canasta.data.api.TogglePurchasedBody
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Servicio singleton in-memory para gestionar listas de compras y sus ítems.
 *
 * La forma de uso está pensada para alinearse con la futura API descrita en swagger:
 *  - ShoppingList ~ definitions.ShoppingList
 *  - ListItem     ~ definitions.ListItem (aquí mapeado a ListProduct dentro de una lista)
 */
object ShoppingListService {

    // Usar el ApiClient correcto que tiene autenticación configurada
    private val api: ShoppingListApi = ApiClient.createService(ShoppingListApi::class.java)
    private val itemsApi: ListItemApi = ApiClient.createService(ListItemApi::class.java)

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
            // La API ahora devuelve un objeto paginado con campos data y pagination
            val response = api.getShoppingLists()
            val remote = response.data
            val mapped = remote.map { dto ->
                // Extraer el emoji/icon del metadata si existe (JsonObject -> JsonElement -> String)
                val icon = dto.metadata?.get("icon")?.jsonPrimitive?.contentOrNull ?: "\uD83D\uDCCB"
                ShoppingList(
                    id = dto.id.toString(),
                    name = dto.name,
                    productCount = 0, // La API no devuelve conteo directo
                    icon = icon,
                    isFavorite = false
                )
            }

            // Obtener los conteos en paralelo para todas las listas
            val updated = coroutineScope {
                mapped.map { list ->
                    async {
                        try {
                            val paged = itemsApi.getItemsForList(
                                listId = list.id.toLong(),
                                page = 1,
                                perPage = 1
                            )
                            val total = paged.pagination.total
                            println("DEBUG: Lista '${list.name}' (id=${list.id}) tiene $total items")
                            list.copy(productCount = total)
                        } catch (e: Exception) {
                            println("Error al obtener conteo para lista ${list.name}: ${e.message}")
                            e.printStackTrace()
                            // En caso de error, mantener el contador en 0
                            list
                        }
                    }
                }.map { it.await() }
            }

            _listsState.value = updated
        } catch (e: Exception) {
            // En caso de error, dejamos el estado actual y logueamos
            println("Error al cargar listas desde API: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Refresca los productos de una lista desde la API y actualiza el cache local.
     * Permite filtrar por categoría (categoryId) si se provee.
     */
    suspend fun refreshProductsForList(listId: String, categoryId: Long? = null) {
        try {
            val response = itemsApi.getItemsForList(
                listId = listId.toLong(),
                categoryId = categoryId
            )
            val dtoItems = response.data
            val totalCount = response.pagination.total // Usar el total de paginación
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
            // Actualizar contador de productos para esa lista usando el total de paginación
            _listsState.value = _listsState.value.map { list ->
                if (list.id == listId) list.copy(productCount = totalCount) else list
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
     * Obtiene todos los productos de una lista directamente desde la API (sin cache)
     * Útil para verificar el estado real de todos los productos
     */
    suspend fun getAllProductsFromApi(listId: String): List<com.example.canasta.data.api.ListItemDto> {
        return try {
            val response = itemsApi.getItemsForList(
                listId = listId.toLong(),
                categoryId = null // Sin filtro
            )
            response.data
        } catch (e: Exception) {
            println("Error al obtener todos los productos de la lista $listId: ${e.message}")
            emptyList()
        }
    }

    /**
     * Crea una nueva lista (usa POST /shopping-lists y luego refresca desde la API).
     *
     * En caso de error HTTP (por ejemplo 409 nombre duplicado) no modifica el estado local
     * y relanza la excepción para que la UI pueda mostrar un mensaje adecuado.
     */
    suspend fun createList(name: String, icon: String?): ShoppingList {
        // Crear metadata con el emoji si está presente
        val metadata = if (icon != null) {
            buildJsonObject {
                put("icon", icon)
            }
        } else {
            buildJsonObject { }
        }

        val body = ShoppingListCreateDto(
            name = name,
            description = "",  // Backend requiere string, no null
            recurring = false,
            metadata = metadata
        )
        try {
            println("DEBUG: Creando lista con body: $body")
            val dto = api.createShoppingList(body)
            println("DEBUG: Lista creada exitosamente: ${dto.id}")

            // Tras crear con éxito en backend, refrescamos desde API para mantener consistencia
            refreshLists()

            return ShoppingList(
                id = dto.id.toString(),
                name = dto.name,
                productCount = 0,
                icon = icon ?: "\uD83D\uDCCB",
                isFavorite = false
            )
        } catch (e: HttpException) {
            println("ERROR HTTP al crear lista en API: code=${e.code()} message=${e.message()}")
            throw e
        } catch (e: Exception) {
            println("ERROR general al crear lista en API: ${e.message}")
            e.printStackTrace()
            throw e
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

    /** Agrega un producto (ListItem) a una lista en backend y refresca la lista */
    suspend fun addProductToListApi(listId: String, productId: Long, quantity: Double = 1.0, unit: String = "unidades") {
        try {
            val body = ListItemCreateDto(
                product = ProductRef(id = productId),
                quantity = quantity,
                unit = unit,
                metadata = null
            )
            itemsApi.addItemToList(listId.toLong(), body)
            refreshProductsForList(listId)
        } catch (e: Exception) {
            println("Error al agregar item a lista $listId en API: ${e.message}")
            throw e
        }
    }

    /** Toggle purchased en backend y refrescar la lista */
    suspend fun toggleItemPurchasedApi(listId: String, itemId: String, purchased: Boolean) {
        try {
            itemsApi.togglePurchased(listId.toLong(), itemId.toLong(), TogglePurchasedBody(purchased))
            // Refrescar items manteniendo el filtro actual no es trivial aquí; refrescamos sin filtro
            refreshProductsForList(listId)
        } catch (e: Exception) {
            println("Error al togglear purchased del item $itemId en lista $listId: ${e.message}")
            throw e
        }
    }
}
