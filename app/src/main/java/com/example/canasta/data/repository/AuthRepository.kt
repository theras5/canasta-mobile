package com.example.canasta.data.repository

import com.example.canasta.data.remote.ApiClient
import com.example.canasta.data.remote.models.*

/**
 * Repository para operaciones de autenticación
 */
class AuthRepository {

    private val authApi = ApiClient.authService

    /**
     * Registra un nuevo usuario
     */
    suspend fun register(
        name: String,
        surname: String,
        email: String,
        password: String
    ): Result<NewUser> {
        return try {
            val user = authApi.register(
                RegistrationData(
                    name = name,
                    surname = surname,
                    email = email,
                    password = password
                )
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<AuthenticationToken> {
        return try {
            val response = authApi.login(Credentials(email, password))
            // Guardar el token en el ApiClient
            ApiClient.setAuthToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifica la cuenta con el código recibido por email
     */
    suspend fun verifyAccount(code: String): Result<NewUser> {
        return try {
            val user = authApi.verifyAccount(VerificationCode(code))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reenvía el código de verificación
     */
    suspend fun resendVerification(email: String): Result<Unit> {
        return try {
            authApi.sendVerification(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra sesión eliminando el token
     */
    fun logout() {
        ApiClient.setAuthToken(null)
    }
}

