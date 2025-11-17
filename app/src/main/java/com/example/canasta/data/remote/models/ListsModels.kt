package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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

@Serializable
data class ShoppingListPaginationDto(
    val total: Int,
    val page: Int,
    val per_page: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean
)

@Serializable
data class ShoppingListPagedResponseDto(
    val data: List<ShoppingListDto>,
    val pagination: ShoppingListPaginationDto
)

@Serializable
data class ShoppingListCreateDto(
    val name: String,
    val description: String? = null,
    val recurring: Boolean? = null,
    val metadata: JsonObject? = null
)

@Serializable
data class ProductDto(
    val id: Long,
    val name: String
)

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

@Serializable
data class ProductRef(
    val id: Long
)

@Serializable
data class ListItemCreateDto(
    val product: ProductRef,
    val quantity: Double,
    val unit: String,
    val metadata: JsonObject? = null
)

@Serializable
data class ListItemPaginationDto(
    val total: Int,
    val page: Int,
    val per_page: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean
)

@Serializable
data class ListItemPagedResponseDto(
    val data: List<ListItemDto>,
    val pagination: ListItemPaginationDto
)

@Serializable
data class TogglePurchasedBody(val purchased: Boolean)

