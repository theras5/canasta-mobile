package com.example.canasta.utils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Utilidades para detectar el tipo de dispositivo
 */
object DeviceUtils {
    /**
     * Detecta si el dispositivo es una tablet basándose en el tamaño de pantalla
     * Se considera tablet si el ancho es mayor o igual a 600dp
     */
    @Composable
    fun isTablet(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp >= 600
    }

    /**
     * Detecta si el dispositivo está en modo landscape
     */
    @Composable
    fun isLandscape(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}

