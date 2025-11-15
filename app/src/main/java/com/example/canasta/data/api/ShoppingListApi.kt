package com.example.canasta.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * DTOs alineados con swagger para listas de compras.
 */
@Serializable
data class ShoppingListDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val recurring: Boolean? = null
)

@Serializable
data class ShoppingListCreateDto(
    val name: String,
    val description: String? = null,
    val recurring: Boolean? = null,
    val metadata: Map<String, String>? = null
)

@Serializable
data class ShoppingListsResponseDto(
    // Según swagger, ShoppingListsArray es un array de ShoppingList
    val items: List<ShoppingListDto>? = null
)

interface ShoppingListApi {

    @GET("/api/shopping-lists")
    suspend fun getShoppingLists(
        @Query("name") name: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): List<ShoppingListDto>

    @POST("/api/shopping-lists")
    suspend fun createShoppingList(
        @Body body: ShoppingListCreateDto
    ): ShoppingListDto
}

