package com.example.canasta.data.remote

import com.example.canasta.data.remote.api.AuthApiService
import com.example.canasta.data.remote.api.CategoryApiService
import com.example.canasta.data.remote.api.ProductApiService
import com.example.canasta.data.remote.api.ShoppingListApiService
import com.example.canasta.data.remote.api.UserApiService
import com.example.canasta.data.remote.interceptors.AuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Cliente singleton para configurar Retrofit y OkHttp
 * Proporciona acceso a los servicios de API
 */
object ApiClient {

    // URL base del servidor
    private const val BASE_URL = "http://10.0.2.2:8080/" // http://localhost:8080/

//    private const val BASE_URL = "http://192.168.0.120:8080/"

    // Configuración de kotlinx-serialization JSON
    private val json = Json {
        ignoreUnknownKeys = true // Ignora campos desconocidos del JSON
        isLenient = true // Permite JSON menos estricto
        coerceInputValues = true // Maneja valores nulos de forma más flexible
    }

    // Interceptor de autenticación (se puede acceder para setear el token)
    val authInterceptor = AuthInterceptor()

    // Cliente OkHttp configurado con interceptors
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Añade autenticación
            .addInterceptor(loggingInterceptor) // Añade logging
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Instancia de Retrofit configurada
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // Servicio de API para productos
    val productService: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }

    // Servicio de API para categorías
    val categoryService: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }

    // Servicio de API para autenticación
    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    // Servicio de API para usuarios
    val userService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    // Servicio de API para shopping lists
    val shoppingListService: ShoppingListApiService by lazy {
        retrofit.create(ShoppingListApiService::class.java)
    }

    /**
     * Método de conveniencia para establecer el token de autenticación
     * @param token Token JWT de autenticación
     */
    fun setAuthToken(token: String?) {
        authInterceptor.setToken(token)
    }

    /**
     * Obtiene el token actual
     */
    fun getAuthToken(): String? = authInterceptor.getToken()

    /**
     * Método genérico para crear cualquier servicio de API con autenticación
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}

