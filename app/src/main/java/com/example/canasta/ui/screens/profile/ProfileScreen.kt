package com.example.canasta.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.components.profile.LogoutButton
import com.example.canasta.ui.components.profile.ProfileHeader
import com.example.canasta.ui.components.profile.ProfileMenuItem

@Composable
fun ProfileScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título "Perfil" alineado a la izquierda
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp, bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header con avatar, nombre y email
            ProfileHeader(
                userName = "Juan Pérez",
                userEmail = "juan.perez@email.com"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Opción de editar perfil
            ProfileMenuItem(
                icon = Icons.Default.Edit,
                text = "Editar Perfil",
                onClick = { /* TODO: Navegar a editar perfil */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción de configuración (nuevo)
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                text = "Configuración",
                onClick = { /* TODO: Navegar a configuración */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de cerrar sesión
            LogoutButton(
                onClick = { /* TODO: Cerrar sesión */ }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
