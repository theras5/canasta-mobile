package com.example.canasta.data.repository

import com.example.canasta.data.remote.ApiClient
import com.example.canasta.data.remote.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar operaciones relacionadas con categorías
 */
class CategoryRepository {

    private val categoryService = ApiClient.categoryService

    /**
     * Obtiene todas las categorías
     */
    suspend fun getCategories(
        name: String? = null,
        page: Int = 1,
        perPage: Int = 100,
        order: String = "ASC",
        sortBy: String = "name"
    ): Result<ArrayOfCategories> = withContext(Dispatchers.IO) {
        try {
            val response = categoryService.getCategories(name, page, perPage, order, sortBy)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva categoría
     */
    suspend fun createCategory(
        name: String,
        metadata: Map<String, String>? = null
    ): Result<GetCategory> = withContext(Dispatchers.IO) {
        try {
            val categoryData = CategoryRegistrationData(name, metadata)
            val category = categoryService.createCategory(categoryData)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene una categoría por su ID
     */
    suspend fun getCategoryById(categoryId: Long): Result<GetCategory> = withContext(Dispatchers.IO) {
        try {
            val category = categoryService.getCategoryById(categoryId)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza una categoría existente
     */
    suspend fun updateCategory(
        id: Long,
        name: String? = null,
        metadata: Map<String, String>? = null
    ): Result<GetCategory> = withContext(Dispatchers.IO) {
        try {
            val updateData = UpdateCategoryProfile(name, metadata)
            val category = categoryService.updateCategory(id, updateData)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una categoría
     */
    suspend fun deleteCategory(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            categoryService.deleteCategory(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

