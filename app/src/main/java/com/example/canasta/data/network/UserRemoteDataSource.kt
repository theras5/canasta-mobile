package com.example.canasta.data.network

import com.example.canasta.data.network.api.RetrofitClient
import com.example.canasta.data.network.model.NetworkCredentials
import com.example.canasta.data.network.model.NetworkToken
import retrofit2.Response

class UserRemoteDataSource {

    private val userApiService = RetrofitClient.userApiService

    suspend fun login(credentials: NetworkCredentials): Response<NetworkToken> {
        return userApiService.login(credentials)
    }

    suspend fun logout(): Response<Unit> {
        return userApiService.logout()
    }
}