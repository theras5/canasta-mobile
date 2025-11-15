package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo para actualizar un producto
 */
@Serializable
data class ProductUpdateData(
    val name: String? = null,
    val category_id: Long? = null,
    val pantry_id: Long? = null,
    val metadata: Map<String, String>? = null
)

/**
 * Respuesta paginada de productos de la API
 */
@Serializable
data class ProductsResponse(
    val data: List<Product>,
    val page: Int? = null,
    val totalPages: Int? = null,
    val total: Int? = null
)

