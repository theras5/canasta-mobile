package com.example.canasta.data.network.api

import com.example.canasta.data.network.api.model.NetworkNewProduct
import com.example.canasta.data.network.api.model.NetworkPagedProducts
import com.example.canasta.data.network.api.model.NetworkProduct
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {
    @POST("products")
    suspend fun createProduct(@Body productData: NetworkNewProduct): Response<NetworkProduct>

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<NetworkPagedProducts>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<NetworkProduct>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body productData: NetworkNewProduct
    ): Response<NetworkProduct>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>
}
