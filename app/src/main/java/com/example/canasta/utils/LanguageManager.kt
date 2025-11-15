package com.example.canasta.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.edit
import java.util.Locale

/**
 * Clase para gestionar el cambio de idioma en la aplicación
 */
object LanguageManager {

    private const val PREF_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    /**
     * Cambia el idioma de la aplicación y reinicia la actividad
     * @param activity Actividad actual
     * @param languageCode Código del idioma (es, en)
     */
    fun setLanguage(activity: Activity, languageCode: String) {
        // Guardar preferencia
        val prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_LANGUAGE, languageCode)
        }

        // Aplicar configuración
        applyLanguage(activity, languageCode)

        // Reiniciar la actividad para aplicar cambios
        val intent = activity.intent
        activity.finish()
        activity.startActivity(intent)
    }

    /**
     * Aplica el idioma configurado al contexto
     * @param context Contexto de la aplicación
     */
    @Suppress("DEPRECATION")
    fun applyLanguage(context: Context, languageCode: String? = null) {
        val lang = languageCode ?: getCurrentLanguage(context)
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.forLanguageTag(lang)
        } else {
            Locale(lang)
        }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * Obtiene el idioma actualmente configurado
     * @param context Contexto de la aplicación
     * @return Código del idioma (es, en)
     */
    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, null) ?: getSystemLanguage()
    }

    /**
     * Obtiene el idioma del sistema
     * @return Código del idioma del sistema
     */
    private fun getSystemLanguage(): String {
        val locale = Locale.getDefault()
        return when (locale.language) {
            "es" -> "es"
            "en" -> "en"
            else -> "es" // Español por defecto
        }
    }
}

