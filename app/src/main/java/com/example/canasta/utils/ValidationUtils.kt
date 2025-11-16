package com.example.canasta.utils

import android.util.Patterns

/**
 * Utilidades para validación de campos de formulario
 */
object ValidationUtils {

    /**
     * Valida que un email tenga formato correcto
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida que la contraseña tenga al menos 6 caracteres
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida que el nombre contenga solo letras (y espacios)
     */
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.all { it.isLetter() || it.isWhitespace() }
    }

    /**
     * Obtiene el mensaje de error para un email inválido
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "El correo electrónico es obligatorio"
            !isValidEmail(email) -> "Ingresa un correo electrónico válido"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para una contraseña inválida
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es obligatoria"
            !isValidPassword(password) -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para un nombre inválido
     */
    fun getNameError(name: String, fieldName: String = "nombre"): String? {
        return when {
            name.isBlank() -> "El $fieldName es obligatorio"
            !isValidName(name) -> "El $fieldName solo puede contener letras"
            else -> null
        }
    }
}

