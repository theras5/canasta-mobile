package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo para crear un producto
 */
@Serializable
data class ProductRegistrationData(
    val name: String,
    val category_id: Long,
    val pantry_id: Long? = null,
    val metadata: Map<String, String>? = null
)


