package com.example.canasta.data.remote.api

import com.example.canasta.data.remote.models.*
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Servicio de API para operaciones de autenticación
 */
interface AuthApiService {

    /**
     * Registra un nuevo usuario
     * POST /api/users/register
     */
    @POST("api/users/register")
    suspend fun register(@Body data: RegistrationData): NewUser

    /**
     * Inicia sesión con credenciales
     * POST /api/users/login
     */
    @POST("api/users/login")
    suspend fun login(@Body credentials: Credentials): AuthenticationToken

    /**
     * Verifica la cuenta de usuario con código
     * POST /api/users/verify-account
     */
    @POST("api/users/verify-account")
    suspend fun verifyAccount(@Body code: VerificationCode): NewUser

    /**
     * Reenvía el código de verificación
     * POST /api/users/send-verification
     */
    @POST("api/users/send-verification")
    suspend fun sendVerification(@Query("email") email: String)

    /**
     * Envía código de recuperación de contraseña
     * POST /api/users/forgot-password
     */
    @POST("api/users/forgot-password")
    suspend fun sendPasswordResetCode(@Query("email") email: String)

    /**
     * Resetea la contraseña usando el código recibido
     * POST /api/users/reset-password
     */
    @POST("api/users/reset-password")
    suspend fun resetPassword(@Body data: PasswordResetData)
}

