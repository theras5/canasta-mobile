package com.example.canasta.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
 * DTO para crear un nuevo item en una lista (según swagger: ListItemCreate)
 */
@Serializable
data class ListItemCreateDto(
    val product_id: Long,  // Nota: usa snake_case como en el swagger
    val quantity: Double,
    val unit: String,
    val metadata: JsonObject? = null
)

/**
 * API para gestionar items de listas de compras
 */
interface ListItemApi {

    /**
     * Obtiene todos los items de una lista
     * GET /api/shopping-lists/{id}/items
     */
    @GET("/api/shopping-lists/{id}/items")
    suspend fun getItemsForList(
        @Path("id") listId: Long
    ): List<ListItemDto>

    /**
     * Agrega un nuevo item a una lista
     * POST /api/shopping-lists/{id}/items
     */
    @POST("/api/shopping-lists/{id}/items")
    suspend fun addItemToList(
        @Path("id") listId: Long,
        @Body item: ListItemCreateDto
    ): ListItemDto
}

