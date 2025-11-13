package com.example.canasta.data.network.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // URL para conectar al backend desde el emulador Android (10.0.2.2 es la IP del host desde el emulador)
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private val authInterceptor = AuthInterceptor()

    // SerializersModule debe ser accesible públicamente
    val serializersModule = SerializersModule {
        contextual(DateSerializer)
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        serializersModule = this@RetrofitClient.serializersModule
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    fun setAuthToken(token: String) {
        authInterceptor.setToken(token)
    }

    fun clearAuthToken() {
        authInterceptor.clearToken()
    }

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> createService(): T = createService(T::class.java)
}
