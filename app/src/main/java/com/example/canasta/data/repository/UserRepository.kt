package com.example.canasta.data.repository

import com.example.canasta.data.remote.ApiClient
import com.example.canasta.data.remote.models.GetUser
import com.example.canasta.data.remote.models.UpdateUserProfile

/**
 * Repository para operaciones de usuario
 */
class UserRepository {

    private val userApi = ApiClient.userService

    /**
     * Obtiene el perfil del usuario autenticado
     */
    suspend fun getUserProfile(): Result<GetUser> {
        return try {
            val user = userApi.getUserProfile()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el perfil del usuario
     */
    suspend fun updateUserProfile(
        name: String? = null,
        surname: String? = null,
        metadata: Map<String, String>? = null
    ): Result<GetUser> {
        return try {
            val updateData = UpdateUserProfile(
                name = name,
                surname = surname,
                metadata = metadata
            )
            val updatedUser = userApi.updateUserProfile(updateData)
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

