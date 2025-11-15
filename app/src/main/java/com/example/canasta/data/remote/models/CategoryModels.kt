package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo para crear una categoría
 */
@Serializable
data class CategoryRegistrationData(
    val name: String,
    val metadata: Map<String, String>? = null
)

/**
 * Modelo para actualizar una categoría
 */
@Serializable
data class UpdateCategoryProfile(
    val name: String? = null,
    val metadata: Map<String, String>? = null
)

/**
 * Respuesta paginada de categorías de la API
 */
@Serializable
data class CategoriesResponse(
    val data: List<GetCategory>,
    val page: Int? = null,
    val totalPages: Int? = null,
    val total: Int? = null
)

/**
 * Tipo alias para un array de categorías
 */
typealias ArrayOfCategories = List<GetCategory>


