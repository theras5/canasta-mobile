package com.example.canasta.data.remote.api

import com.example.canasta.data.remote.models.ArrayOfProducts
import com.example.canasta.data.remote.models.GetProduct
import com.example.canasta.data.remote.models.ProductRegistrationData
import retrofit2.http.*

/**
 * Servicio de API para operaciones relacionadas con productos
 */
interface ProductApiService {

    /**
     * Obtiene todos los productos
     * GET /products
     */
    @GET("products")
    suspend fun getProducts(): ArrayOfProducts

    /**
     * Crea un nuevo producto
     * POST /products
     * @param product Datos del producto a crear
     */
    @POST("products")
    suspend fun createProduct(@Body product: ProductRegistrationData): GetProduct

    /**
     * Obtiene un producto específico por su ID
     * GET /products/{productId}
     * @param productId ID del producto
     */
    @GET("products/{productId}")
    suspend fun getProduct(@Path("productId") productId: Long): GetProduct

    /**
     * Actualiza un producto existente
     * PUT /products/{productId}
     * @param productId ID del producto a actualizar
     * @param product Datos actualizados del producto
     */
    @PUT("products/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: Long,
        @Body product: ProductRegistrationData
    ): GetProduct

    /**
     * Elimina un producto
     * DELETE /products/{productId}
     * @param productId ID del producto a eliminar
     */
    @DELETE("products/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: Long)
}

