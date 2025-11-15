    package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo para crear o actualizar un producto
 */
@Serializable
data class ProductRegistrationData(
    val name: String,
    val category: CategoryReference? = null,
    val metadata: Map<String, String>? = null
)

/**
 * Referencia a una categoría por su ID
 */
@Serializable
data class CategoryReference(
    val id: Long
)

