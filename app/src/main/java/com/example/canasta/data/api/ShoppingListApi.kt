package com.example.canasta.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * DTOs alineados con swagger para listas de compras.
 */

/**
 * Usuario simplificado (owner de la lista)
 */
@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val metadata: JsonObject? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * ShoppingList completo según swagger
 */
@Serializable
data class ShoppingListDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val recurring: Boolean? = null,
    val metadata: JsonObject? = null,
    val owner: UserDto? = null,
    val sharedWith: List<UserDto>? = null,
    val lastPurchasedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Información de paginación devuelta por la API de listas de compras.
 */
@Serializable
data class ShoppingListPaginationDto(
    val total: Int,
    val page: Int,
    val per_page: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean
)

/**
 * Respuesta paginada de la API de listas de compras.
 */
@Serializable
data class ShoppingListPagedResponseDto(
    val data: List<ShoppingListDto>,
    val pagination: ShoppingListPaginationDto
)

/**
 * DTO para crear una shopping list
 */
@Serializable
data class ShoppingListCreateDto(
    val name: String,
    val description: String? = null,
    val recurring: Boolean? = null,
    val metadata: JsonObject? = null
)


interface ShoppingListApi {

    @GET("/api/shopping-lists")
    suspend fun getShoppingLists(
        @Query("name") name: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): ShoppingListPagedResponseDto

    @POST("/api/shopping-lists")
    suspend fun createShoppingList(
        @Body body: ShoppingListCreateDto
    ): ShoppingListDto
}
