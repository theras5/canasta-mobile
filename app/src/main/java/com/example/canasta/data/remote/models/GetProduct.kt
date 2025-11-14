package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo de producto que se recibe del servidor (GET)
 */
@Serializable
data class GetProduct(
    val id: Long,
    val name: String,
    val metadata: Map<String, String>? = null,
    val createdAt: String,
    val updatedAt: String,
    val category: GetCategory
)

