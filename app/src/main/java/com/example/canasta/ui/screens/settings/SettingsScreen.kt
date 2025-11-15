package com.example.canasta.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.data.repository.UserRepository
import com.example.canasta.ui.components.settings.ChangePasswordDialog
import com.example.canasta.ui.components.settings.SettingsItem
import com.example.canasta.ui.components.settings.SettingsSection
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }
    var passwordErrorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {

            // Título "Configuración" alineado a la izquierda
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtítulo
            Text(
                text = "Personaliza tu experiencia en Canasta",
                fontSize = 16.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Sección: Cuenta
            SettingsSection(
                title = "Cuenta"
            ) {
                SettingsItem(
                    title = "Cambiar contraseña",
                    subtitle = "Actualiza tu contraseña de acceso",
                    onClick = { showChangePasswordDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsItem(
                    title = "Cambiar idioma",
                    subtitle = "Selecciona tu idioma preferido",
                    onClick = { /* TODO: Navegar a cambiar idioma */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog de cambiar contraseña
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = {
                if (!isChangingPassword) {
                    showChangePasswordDialog = false
                    passwordErrorMessage = null
                }
            },
            isLoading = isChangingPassword,
            errorMessage = passwordErrorMessage,
            onConfirm = { currentPassword, newPassword, confirmPassword ->
                // No permitir múltiples llamadas mientras se procesa
                if (isChangingPassword) return@ChangePasswordDialog

                // Limpiar errores previos
                passwordErrorMessage = null

                // Validar que las contraseñas coincidan
                if (newPassword != confirmPassword) {
                    passwordErrorMessage = "Las contraseñas no coinciden"
                    return@ChangePasswordDialog
                }

                // Validar longitud mínima
                if (newPassword.length < 6) {
                    passwordErrorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@ChangePasswordDialog
                }

                // Llamar a la API
                isChangingPassword = true
                scope.launch {
                    userRepository.changePassword(
                        currentPassword = currentPassword,
                        newPassword = newPassword
                    ).fold(
                        onSuccess = {
                            isChangingPassword = false
                            showChangePasswordDialog = false
                            passwordErrorMessage = null
                            snackbarHostState.showSnackbar("Contraseña actualizada exitosamente")
                        },
                        onFailure = { error ->
                            isChangingPassword = false
                            passwordErrorMessage = when {
                                error.message?.contains("401") == true -> "Contraseña actual incorrecta"
                                error.message?.contains("400") == true -> "Datos inválidos"
                                else -> "Error al cambiar contraseña: ${error.message}"
                            }
                        }
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}

