package com.example.canasta.utils

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Clase para gestionar el cambio de idioma en la aplicación
 */
object LanguageManager {

    private const val PREF_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val SYSTEM_LANGUAGE = "system"

    private val _languageChangeCounter = MutableStateFlow(0L)
    val languageChangeCounter: StateFlow<Long> = _languageChangeCounter.asStateFlow()

    /**
     * Cambia el idioma de la aplicación sin reiniciar
     * @param context Contexto de la aplicación
     * @param languageCode Código del idioma (es, en, system)
     */
    fun setLanguage(context: Context, languageCode: String) {
        Log.d("LanguageManager", "setLanguage called with: $languageCode")

        // Guardar preferencia
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_LANGUAGE, languageCode)
        }

        // Verificar que se guardó correctamente
        val saved = prefs.getString(KEY_LANGUAGE, null)
        Log.d("LanguageManager", "Saved language: $saved")

        // Aplicar configuración
        applyLanguage(context, languageCode)

        // Notificar cambio para recomposición
        _languageChangeCounter.value = System.currentTimeMillis()
    }

    /**
     * Aplica el idioma configurado al contexto
     * @param context Contexto de la aplicación
     */
    @Suppress("DEPRECATION")
    fun applyLanguage(context: Context, languageCode: String? = null) {
        val lang = languageCode ?: getCurrentLanguage(context)

        // Si es "system", usar el idioma del sistema
        val actualLang = if (lang == SYSTEM_LANGUAGE) {
            getSystemLanguage()
        } else {
            lang
        }

        val locale = Locale.forLanguageTag(actualLang)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * Obtiene el idioma actualmente configurado
     * @param context Contexto de la aplicación
     * @return Código del idioma (es, en, system)
     */
    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val language = prefs.getString(KEY_LANGUAGE, SYSTEM_LANGUAGE) ?: SYSTEM_LANGUAGE
        Log.d("LanguageManager", "getCurrentLanguage: $language")
        return language
    }

    /**
     * Obtiene el idioma del sistema
     * @return Código del idioma del sistema (es o en)
     */
    private fun getSystemLanguage(): String {
        val locale = Locale.getDefault()
        return when (locale.language) {
            "es" -> "es"
            "en" -> "en"
            else -> "en" // Inglés por defecto si no es español
        }
    }
}

