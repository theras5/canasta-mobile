package com.example.canasta.data.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Cliente Retrofit centralizado para la API del Grocery Manager.
 *
 * Usa Kotlinx Serialization como convertidor JSON y OkHttp con logging.
 * El interceptor de auth está preparado para añadir el header Authorization
 * cuando se integre el flujo de autenticación.
 */
object ApiClient {

    // Para emulador Android: 10.0.2.2 apunta al localhost de la máquina host
    private const val BASE_URL = "http://10.0.2.2:8080"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        // TODO: inyectar token real cuando exista sesión
        // val token = ...
        // if (token != null) builder.header("Authorization", "Bearer $token")
        chain.proceed(builder.build())
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
