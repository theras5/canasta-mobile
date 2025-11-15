package com.example.canasta.ui.screens.auth

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.R
import com.example.canasta.ui.components.common.AppScaffold
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles


@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val viewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(context.applicationContext as Application) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    // Navegar a home cuando se autentique
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateToHome()
        }
    }

    AppScaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    })
                },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(0.85f),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono de canasta
                    Image(
                        painter = painterResource(id = R.drawable.logohd),
                        contentDescription = "Canasta Icon",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.isVerificationMode) {
                        VerificationContent(
                            email = uiState.email,
                            verificationCode = uiState.verificationCode,
                            onCodeChange = viewModel::updateVerificationCode,
                            onVerify = viewModel::verifyAccount,
                            onResend = viewModel::resendVerification,
                            onBackToRegister = viewModel::backToRegister,
                            errorMessage = uiState.errorMessage,
                            isLoading = uiState.isLoading,
                            keyboardController = keyboardController,
                            focusManager = focusManager
                        )
                    } else {
                        AuthContent(
                            isLoginMode = uiState.isLoginMode,
                            email = uiState.email,
                            password = uiState.password,
                            name = uiState.name,
                            surname = uiState.surname,
                            onEmailChange = viewModel::updateEmail,
                            onPasswordChange = viewModel::updatePassword,
                            onNameChange = viewModel::updateName,
                            onSurnameChange = viewModel::updateSurname,
                            onToggleMode = viewModel::toggleMode,
                            onLogin = viewModel::login,
                            onRegister = viewModel::register,
                            errorMessage = uiState.errorMessage,
                            isLoading = uiState.isLoading,
                            keyboardController = keyboardController,
                            focusManager = focusManager
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerificationContent(
    email: String,
    verificationCode: String,
    onCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onBackToRegister: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Text(
        text = "Verificar tu cuenta",
        style = MaterialTheme.typography.headlineMedium,
        color = Titles,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "código de verificación enviado a",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Text(
        text = email,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Ingresa el código para activar tu cuenta",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))

    // Campo de código de verificación
    OutlinedTextField(
        value = verificationCode,
        onValueChange = onCodeChange,
        label = { Text("Código de verificación") },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        isError = errorMessage != null && !errorMessage.contains("reenviado"),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        singleLine = true
    )

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = if (errorMessage.contains("reenviado") || errorMessage.contains("verificada"))
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Botón verificar cuenta
    Button(
        onClick = onVerify,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("VERIFICAR CUENTA")
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Enlaces de verificación
    Text(
        text = "¿No recibiste el código?",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Text(
        text = "Reenviar código",
        style = MaterialTheme.typography.bodySmall,
        color = Secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable(enabled = !isLoading) { onResend() }
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Volver al registro",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.clickable(enabled = !isLoading) { onBackToRegister() }
    )
}

@Composable
private fun AuthContent(
    isLoginMode: Boolean,
    email: String,
    password: String,
    name: String,
    surname: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Text(
        text = if (isLoginMode) "Iniciar Sesión" else "Crear Cuenta",
        style = MaterialTheme.typography.headlineMedium,
        color = Titles
    )
    Text(
        text = if (isLoginMode) "Accede a tu cuenta de Canasta" else "Organiza tus compras con canasta",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = 4.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Campos de texto
    if (!isLoginMode) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            enabled = !isLoading,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        OutlinedTextField(
            value = surname,
            onValueChange = onSurnameChange,
            label = { Text("Apellido") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = !isLoading,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
    }
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Correo electrónico") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        enabled = !isLoading,
        isError = errorMessage != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(
                "Contraseña (mínimo 6 caracteres)",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        visualTransformation = PasswordVisualTransformation(),
        enabled = !isLoading,
        isError = errorMessage != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        singleLine = true
    )

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = if (errorMessage.contains("verificada"))
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Botón crear cuenta/iniciar sesión
    Button(
        onClick = if (isLoginMode) onLogin else onRegister,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(if (isLoginMode) "INICIAR SESIÓN" else "CREAR CUENTA")
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    // Links de navegación
    if (isLoginMode) {
        Text(
            text = "¿No tienes cuenta?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Registrarse",
            style = MaterialTheme.typography.bodySmall,
            color = Secondary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(enabled = !isLoading) { onToggleMode() }
        )
    } else {
        Text(
            text = "¿Ya tienes cuenta?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.bodySmall,
            color = Secondary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(enabled = !isLoading) { onToggleMode() }
        )
    }
}
