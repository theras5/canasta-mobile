package com.example.canasta.data.network.api

import com.example.canasta.data.network.model.NetworkCategory
import com.example.canasta.data.network.model.NetworkNewCategory
import com.example.canasta.data.network.model.NetworkPagedCategories
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CategoryApiService {
    @POST("categories")
    suspend fun createCategory(@Body categoryData: NetworkNewCategory): Response<NetworkCategory>

    @GET("categories")
    suspend fun getCategories(): Response<NetworkPagedCategories>
}

