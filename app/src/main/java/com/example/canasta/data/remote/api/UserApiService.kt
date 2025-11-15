package com.example.canasta.data.remote.api

import com.example.canasta.data.remote.models.GetUser
import com.example.canasta.data.remote.models.UpdateUserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * Servicio de API para operaciones de usuario
 */
interface UserApiService {

    /**
     * Obtiene el perfil del usuario autenticado
     * GET /api/users/profile
     */
    @GET("api/users/profile")
    suspend fun getUserProfile(): GetUser

    /**
     * Actualiza el perfil del usuario
     * PUT /api/users/profile
     */
    @PUT("api/users/profile")
    suspend fun updateUserProfile(@Body data: UpdateUserProfile): GetUser
}

