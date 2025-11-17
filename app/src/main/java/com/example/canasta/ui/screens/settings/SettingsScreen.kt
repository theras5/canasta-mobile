package com.example.canasta.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.R
import com.example.canasta.data.repository.UserRepository
import com.example.canasta.ui.components.common.CommonScreenHeader
import com.example.canasta.ui.components.settings.ChangeLanguageDialog
import com.example.canasta.ui.components.settings.ChangePasswordDialog
import com.example.canasta.ui.components.settings.SettingsItem
import com.example.canasta.ui.components.settings.SettingsSection
import com.example.canasta.ui.theme.Background
import com.example.canasta.utils.LanguageManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }
    var passwordErrorMessage by remember { mutableStateOf<String?>(null) }

    var showChangeLanguageDialog by remember { mutableStateOf(false) }

    // Observar cambios de idioma para actualizar el diálogo
    val languageChangeCounter by LanguageManager.languageChangeCounter.collectAsState()
    val currentLanguage = remember(languageChangeCounter) {
        LanguageManager.getCurrentLanguage(context)
    }

    Scaffold(
        containerColor = Background,
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
            CommonScreenHeader(title = stringResource(R.string.settings))

            // Subtítulo
            Text(
                text = stringResource(R.string.settings_subtitle),
                fontSize = 16.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Sección: Cuenta
            SettingsSection(
                title = stringResource(R.string.account_section)
            ) {
                SettingsItem(
                    title = stringResource(R.string.change_password),
                    subtitle = stringResource(R.string.change_password_subtitle),
                    onClick = { showChangePasswordDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsItem(
                    title = stringResource(R.string.change_language),
                    subtitle = stringResource(R.string.change_language_subtitle),
                    onClick = { showChangeLanguageDialog = true }
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
                    passwordErrorMessage = context.getString(R.string.passwords_dont_match)
                    return@ChangePasswordDialog
                }

                // Validar longitud mínima
                if (newPassword.length < 6) {
                    passwordErrorMessage = context.getString(R.string.password_too_short)
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
                            snackbarHostState.showSnackbar(context.getString(R.string.password_changed_successfully))
                        },
                        onFailure = { error ->
                            isChangingPassword = false
                            passwordErrorMessage = when {
                                error.message?.contains("401") == true -> context.getString(R.string.incorrect_current_password)
                                error.message?.contains("400") == true -> context.getString(R.string.invalid_data)
                                else -> context.getString(R.string.error_changing_password, error.message ?: "")
                            }
                        }
                    )
                }
            }
        )
    }

    // Dialog de cambiar idioma
    if (showChangeLanguageDialog) {
        ChangeLanguageDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showChangeLanguageDialog = false },
            onConfirm = { languageCode ->
                LanguageManager.setLanguage(context, languageCode)
                showChangeLanguageDialog = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}
