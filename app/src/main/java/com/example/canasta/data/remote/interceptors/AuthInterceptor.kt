package com.example.canasta.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor para añadir el token de autenticación a todas las peticiones
 */
class AuthInterceptor : Interceptor {

    // TODO: Implementar mecanismo para obtener el token de manera dinámica
    // Por ejemplo, desde SharedPreferences o un TokenManager
    private var authToken: String? = null

    /**
     * Establece el token de autenticación
     */
    fun setToken(token: String?) {
        authToken = token
    }

    /**
     * Obtiene el token actual
     */
    fun getToken(): String? = authToken

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si no hay token, proceder sin autenticación
        if (authToken.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Añadir el header Authorization con el token Bearer
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $authToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}

