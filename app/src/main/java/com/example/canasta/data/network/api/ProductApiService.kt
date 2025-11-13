package com.example.canasta.data.network.api

import com.example.canasta.data.network.model.NetworkNewProduct
import com.example.canasta.data.network.model.NetworkProduct
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProductApiService {
    @POST("products")
    suspend fun createProduct(@Body product: NetworkNewProduct): Response<NetworkProduct>
}

