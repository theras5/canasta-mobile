package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo de producto básico
 */
@Serializable
data class Product(
    val id: Long,
    val name: String,
    val metadata: Map<String, String>? = null,
    val createdAt: String,
    val updatedAt: String,
    val category: GetCategory
)

