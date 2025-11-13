package com.example.canasta.data.network.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private var authToken: String? = null) : Interceptor {

    fun setToken(token: String) {
        authToken = token
    }

    fun clearToken() {
        authToken = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return if (authToken != null) {
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(request)
        }
    }
}
