package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo para crear una shopping list
 */
@Serializable
data class ShoppingListCreate(
    val name: String,
    val description: String? = null,
    val recurring: Boolean = false,
    val metadata: Map<String, String>? = null
)

/**
 * Modelo para actualizar una shopping list
 */
@Serializable
data class ShoppingListUpdate(
    val name: String? = null,
    val description: String? = null,
    val recurring: Boolean? = null,
    val metadata: Map<String, String>? = null
)

/**
 * Modelo de respuesta de shopping list
 */
@Serializable
data class ShoppingListResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val recurring: Boolean,
    val metadata: Map<String, String>? = null,
    val createdAt: String,
    val updatedAt: String,
    val lastPurchasedAt: String? = null
)

/**
 * Modelo de respuesta paginada para shopping lists
 */
@Serializable
data class ShoppingListsPaginated(
    val data: List<ShoppingListResponse>,
    val pagination: Pagination
)

/**
 * Modelo de paginación
 */
@Serializable
data class Pagination(
    val total: Int,
    val page: Int,
    val per_page: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean
)

