package com.example.canasta.utils

import android.content.Context
import android.util.Patterns
import com.example.canasta.R

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
    fun getEmailError(email: String, context: Context): String? {
        return when {
            email.isBlank() -> context.getString(R.string.error_email_required)
            !isValidEmail(email) -> context.getString(R.string.error_email_invalid)
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para una contraseña inválida
     */
    fun getPasswordError(password: String, context: Context): String? {
        return when {
            password.isBlank() -> context.getString(R.string.error_password_required)
            !isValidPassword(password) -> context.getString(R.string.error_password_too_short)
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para un nombre inválido
     */
    fun getNameError(name: String, fieldName: String, context: Context): String? {
        return when {
            name.isBlank() -> context.getString(R.string.error_name_required, fieldName)
            !isValidName(name) -> context.getString(R.string.error_name_invalid, fieldName)
            else -> null
        }
    }
}

