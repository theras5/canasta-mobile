package ar.edu.itba.example.api.data.network.api

import ar.edu.itba.example.api.data.network.model.NetworkCategory
import ar.edu.itba.example.api.data.network.model.NetworkNewCategory
import ar.edu.itba.example.api.data.network.model.NetworkPagedCategories
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

