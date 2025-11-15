package com.example.canasta.data.remote.api

import com.example.canasta.data.remote.models.ArrayOfProducts
import com.example.canasta.data.remote.models.Product
import com.example.canasta.data.remote.models.ProductRegistrationData
import com.example.canasta.data.remote.models.ProductUpdateData
import com.example.canasta.data.remote.models.ProductsResponse
import retrofit2.http.*

/**
 * Servicio de API para operaciones relacionadas con productos
 */
interface ProductApiService {

    /**
     * Obtiene todos los productos
     * GET /api/products
     */
    @GET("api/products")
    suspend fun getProducts(
        @Query("name") name: String? = null,
        @Query("category_id") categoryId: Long? = null,
        @Query("pantry_id") pantryId: Long? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("sort_by") sortBy: String = "name",
        @Query("order") order: String = "asc"
    ): ProductsResponse

    /**
     * Crea un nuevo producto
     * POST /api/products
     * @param product Datos del producto a crear
     */
    @POST("api/products")
    suspend fun createProduct(@Body product: ProductRegistrationData): Product

    /**
     * Obtiene un producto específico por su ID
     * GET /api/products/{id}
     * @param id ID del producto
     */
    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Product

    /**
     * Actualiza un producto existente
     * PUT /api/products/{id}
     * @param id ID del producto a actualizar
     * @param product Datos actualizados del producto
     */
    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body product: ProductUpdateData
    ): Product

    /**
     * Elimina un producto
     * DELETE /api/products/{id}
     * @param id ID del producto a eliminar
     */
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long)
}

