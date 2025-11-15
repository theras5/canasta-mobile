package com.example.canasta.data.remote.api

import com.example.canasta.data.remote.models.*
import retrofit2.http.*

/**
 * Servicio de API para operaciones relacionadas con categorías
 */
interface CategoryApiService {

    /**
     * Obtiene todas las categorías del usuario
     * GET /api/categories
     */
    @GET("api/categories")
    suspend fun getCategories(
        @Query("name") name: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("order") order: String = "ASC",
        @Query("sort_by") sortBy: String = "createdAt"
    ): CategoriesResponse

    /**
     * Crea una nueva categoría
     * POST /api/categories
     * @param category Datos de la categoría a crear
     */
    @POST("api/categories")
    suspend fun createCategory(@Body category: CategoryRegistrationData): GetCategory

    /**
     * Obtiene una categoría específica por su ID
     * GET /api/categories/{categoryId}
     * @param categoryId ID de la categoría
     */
    @GET("api/categories/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") categoryId: Long): GetCategory

    /**
     * Actualiza una categoría existente
     * PUT /api/categories/{id}
     * @param id ID de la categoría a actualizar
     * @param category Datos actualizados de la categoría
     */
    @PUT("api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Long,
        @Body category: UpdateCategoryProfile
    ): GetCategory

    /**
     * Elimina una categoría
     * DELETE /api/categories/{id}
     * @param id ID de la categoría a eliminar
     */
    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long)
}

