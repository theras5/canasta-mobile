package com.example.canasta.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

@Serializable
data class ListItemDto(
    val id: Long,
    val quantity: Double? = null,
    val unit: String? = null,
    val purchased: Boolean? = null,
    val product: ProductDto
)

@Serializable
data class ProductDto(
    val id: Long,
    val name: String
)

interface ListItemApi {

    @GET("/api/shopping-lists/{id}/items")
    suspend fun getItemsForList(
        @Path("id") listId: Long
    ): List<ListItemDto>
}

