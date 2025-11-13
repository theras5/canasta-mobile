package com.example.canasta.data.network.api

import com.example.canasta.data.network.model.NetworkCredentials
import com.example.canasta.data.network.model.NetworkToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("users/login")
    suspend fun login(@Body credentials: NetworkCredentials): Response<NetworkToken>

    @POST("users/logout")
    suspend fun logout(): Response<Unit>
}