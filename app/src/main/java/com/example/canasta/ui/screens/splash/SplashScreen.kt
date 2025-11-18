package com.example.canasta.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.R
import com.example.canasta.data.local.TokenManager
import com.example.canasta.ui.theme.Primary
import com.example.canasta.data.remote.ApiClient
import kotlinx.coroutines.delay

/**
 * Pantalla de bienvenida (Splash Screen)
 * Muestra el logo de Canasta durante 2 segundos y verifica si hay sesión activa
 * - Si hay sesión: navega a la pantalla de listas
 * - Si no hay sesión: navega al login
 */
@Composable
fun SplashScreen(
    onNavigateToLists: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)

    // Verificar sesión después de 2 segundos
    LaunchedEffect(Unit) {
        delay(2000) // 2 segundos de splash

        // Verificar si hay token guardado
        val hasSession = tokenManager.hasActiveSession()

        if (hasSession) {
            // Restaurar el token en ApiClient
            val token = tokenManager.getToken()
            ApiClient.setAuthToken(token)
            onNavigateToLists()
        } else {
            onNavigateToLogin()
        }
    }

    // Diseño de la pantalla
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo de Canasta
        Image(
            painter = painterResource(id = R.drawable.logohd),
            contentDescription = "Logo de Canasta",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Texto "Canasta"
        Text(
            text = "Canasta",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}

