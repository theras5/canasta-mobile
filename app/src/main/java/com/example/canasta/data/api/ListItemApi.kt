package com.example.canasta.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * DTO simplificado del producto dentro de un ListItem
 */
@Serializable
data class ProductDto(
    val id: Long,
    val name: String
)

/**
 * DTO para un item de la lista (según swagger: ListItem)
 */
@Serializable
data class ListItemDto(
    val id: Long,
    val quantity: Double? = null,
    val unit: String? = null,
    val purchased: Boolean? = null,
    val metadata: JsonObject? = null,
    val lastPurchasedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val product: ProductDto
)

/**
 * Referencia de producto para creación: { product: { id } }
 */
@Serializable
data class ProductRef(
    val id: Long
)

/**
 * DTO para crear un nuevo item en una lista (según swagger: ListItemCreate)
 * Estructura esperada:
 * {
 *   "product": { "id": number },
 *   "quantity": number (>0),
 *   "unit": string (non-empty),
 *   "metadata"?: object
 * }
 */
@Serializable
data class ListItemCreateDto(
    val product: ProductRef,
    val quantity: Double,
    val unit: String,
    val metadata: JsonObject? = null
)

/**
 * Información de paginación devuelta por la API de items de lista.
 */
@Serializable
data class ListItemPaginationDto(
    val total: Int,
    val page: Int,
    val per_page: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean
)

/**
 * Respuesta paginada de items de una lista.
 */
@Serializable
data class ListItemPagedResponseDto(
    val data: List<ListItemDto>,
    val pagination: ListItemPaginationDto
)

/** Body para toggle purchased */
@Serializable
data class TogglePurchasedBody(val purchased: Boolean)

/**
 * API para gestionar items de listas de compras
 */
interface ListItemApi {

    /**
     * Obtiene items de una lista con filtros opcionales (paginado)
     * GET /api/shopping-lists/{id}/items
     */
    @GET("/api/shopping-lists/{id}/items")
    suspend fun getItemsForList(
        @Path("id") listId: Long,
        @Query("purchased") purchased: Boolean? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("order") order: String? = null,
        @Query("pantry_id") pantryId: Long? = null,
        @Query("category_id") categoryId: Long? = null,
        @Query("search") search: String? = null
    ): ListItemPagedResponseDto

    /**
     * Agrega un nuevo item a una lista
     * POST /api/shopping-lists/{id}/items
     */
    @POST("/api/shopping-lists/{id}/items")
    suspend fun addItemToList(
        @Path("id") listId: Long,
        @Body item: ListItemCreateDto
    ): ListItemDto

    /**
     * Cambia el estado de purchased de un item
     * PATCH /api/shopping-lists/{id}/items/{item_id}
     */
    @PATCH("/api/shopping-lists/{id}/items/{item_id}")
    suspend fun togglePurchased(
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body body: TogglePurchasedBody
    ): ListItemDto
}
