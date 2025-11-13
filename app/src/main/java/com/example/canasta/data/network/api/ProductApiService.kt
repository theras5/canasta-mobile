package ar.edu.itba.example.api.data.network.api

import ar.edu.itba.example.api.data.network.model.NetworkNewProduct
import ar.edu.itba.example.api.data.network.model.NetworkProduct
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProductApiService {
    @POST("products")
    suspend fun createProduct(@Body product: NetworkNewProduct): Response<NetworkProduct>
}

