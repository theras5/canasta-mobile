package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo para crear un producto
 */
@Serializable
data class ProductRegistrationData(
    val name: String,
    val category: CategoryRef? = null,
    val metadata: Map<String, String>? = null
)

@Serializable
data class CategoryRef(val id: Long)
