package com.example.canasta.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de tokens de autenticación usando SharedPreferences
 * Proporciona persistencia de la sesión del usuario
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "canasta_auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    /**
     * Guarda el token de autenticación
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    /**
     * Obtiene el token de autenticación guardado
     */
    fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Elimina el token (logout)
     */
    fun clearToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun hasActiveSession(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}

