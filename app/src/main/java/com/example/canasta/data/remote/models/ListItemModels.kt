package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo para crear un item en la lista
 */
@Serializable
data class ListItemCreate(
    val product: ProductId,
    val quantity: Double = 1.0,
    val unit: String = "unidades",
    val metadata: Map<String, String>? = null
)

/**
 * Modelo para actualizar un item en la lista
 */
@Serializable
data class ListItemUpdate(
    val quantity: Double? = null,
    val unit: String? = null,
    val metadata: Map<String, String>? = null
)

/**
 * Modelo para el ID de un producto
 */
@Serializable
data class ProductId(
    val id: Long
)

/**
 * Modelo de respuesta de un list item
 */
@Serializable
data class ListItemResponse(
    val id: Long,
    val quantity: Double,
    val unit: String,
    val metadata: Map<String, String>? = null,
    val purchased: Boolean,
    val lastPurchasedAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val product: Product
)

/**
 * Modelo de respuesta paginada para list items
 */
@Serializable
data class ListItemsPaginated(
    val data: List<ListItemResponse>,
    val pagination: Pagination
)

/**
 * Modelo para toggle purchased status
 */
@Serializable
data class TogglePurchasedRequest(
    val purchased: Boolean
)

