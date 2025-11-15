package com.example.canasta.data.repository

import com.example.canasta.data.remote.ApiClient
import com.example.canasta.data.remote.models.CategoryRef
import com.example.canasta.data.remote.models.Product
import com.example.canasta.data.remote.models.ProductRegistrationData
import com.example.canasta.data.remote.models.ProductUpdateData
import com.example.canasta.data.remote.models.UpdateProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar operaciones relacionadas con productos
 */
class ProductRepository {

    private val productService = ApiClient.productService

    /**
     * Obtiene todos los productos
     */
    suspend fun getProducts(
        name: String? = null,
        categoryId: Long? = null,
        pantryId: Long? = null,
        page: Int = 1,
        perPage: Int = 100,
        sortBy: String = "name",
        order: String = "asc"
    ): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = productService.getProducts(name, categoryId, pantryId, page, perPage, sortBy, order)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea un nuevo producto
     */
    suspend fun createProduct(
        name: String,
        categoryId: Long?,
        metadata: Map<String, String>? = null
    ): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val productData = ProductRegistrationData(
                name = name,
                category = categoryId?.let { CategoryRef(it) },
                metadata = metadata
            )
            val product = productService.createProduct(productData)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un producto por su ID
     */
    suspend fun getProductById(id: Long): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val product = productService.getProductById(id)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un producto existente
     */
    suspend fun updateProduct(
        id: Long,
        name: String? = null,
        categoryId: Long? = null,
        metadata: Map<String, String>? = null
    ): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val updateData = ProductUpdateData(
                name = name,
                category = categoryId?.let { CategoryRef(it) },
                metadata = metadata
            )
            val response = productService.updateProduct(id, updateData)
            val product = response.product ?: throw IllegalStateException("Respuesta sin producto")
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un producto
     */
    suspend fun deleteProduct(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            productService.deleteProduct(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
