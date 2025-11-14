package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo de categoría que se recibe del servidor
 */
@Serializable
data class GetCategory(
    val id: Long,
    val name: String,
    val metadata: Map<String, String>? = null,
    val updatedAt: String,
    val createdAt: String
)

