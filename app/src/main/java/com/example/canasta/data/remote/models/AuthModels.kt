package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Datos para registro de usuario
 */
@Serializable
data class RegistrationData(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val metadata: Map<String, String>? = null
)

/**
 * Credenciales de login
 */
@Serializable
data class Credentials(
    val email: String,
    val password: String
)

/**
 * Código de verificación
 */
@Serializable
data class VerificationCode(
    val code: String
)

/**
 * Token de autenticación retornado por login
 */
@Serializable
data class AuthenticationToken(
    val token: String
)

/**
 * Usuario recién creado o verificado
 */
@Serializable
data class NewUser(
    val id: Long,
    val email: String,
    val name: String,
    val surname: String,
    val metadata: Map<String, String>? = null,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Datos de perfil de usuario
 */
@Serializable
data class GetUser(
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val metadata: Map<String, String>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Datos para actualizar el perfil de usuario
 */
@Serializable
data class UpdateUserProfile(
    val name: String? = null,
    val surname: String? = null,
    val metadata: Map<String, String>? = null
)

/**
 * Datos para cambiar contraseña
 */
@Serializable
data class PasswordChange(
    val currentPassword: String,
    val newPassword: String
)

/**
 * Datos para resetear contraseña
 */
@Serializable
data class PasswordResetData(
    val code: String,
    val password: String
)

