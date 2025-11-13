package com.example.canasta.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.R
import com.example.canasta.ui.components.common.AppScaffold
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Efecto para navegar cuando el login sea exitoso
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    AppScaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Canasta Icon",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!uiState.isVerified) {
                        // Pantalla de verificación
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
                            text = uiState.email.ifEmpty { "tu correo electrónico" },
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
                            value = uiState.verificationCode,
                            onValueChange = viewModel::onVerificationCodeChanged,
                            label = { Text("Código de verificación") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón verificar cuenta
                        Button(
                            onClick = { /* TODO: Implementar verificación */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            enabled = !uiState.isLoading && uiState.verificationCode.isNotEmpty()
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
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
                                .clickable { /* TODO: Implementar reenvío */ }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Volver al registro",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable { viewModel.switchMode() }
                        )
                    } else {
                        // Pantalla de login/registro principal
                        Text(
                            text = if (uiState.isLoginMode) "Iniciar Sesión" else "Crear Cuenta",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Titles
                        )
                        Text(
                            text = if (uiState.isLoginMode) "Accede a tu cuenta de Canasta" else "organiza tus compras con canasta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mostrar error si existe
                        uiState.error?.let { error ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Campos de texto
                        if (!uiState.isLoginMode) {
                            OutlinedTextField(
                                value = uiState.firstName,
                                onValueChange = viewModel::onFirstNameChanged,
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoading,
                                isError = uiState.firstName.isEmpty() && uiState.error != null
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.lastName,
                                onValueChange = viewModel::onLastNameChanged,
                                label = { Text("Apellido") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoading,
                                isError = uiState.lastName.isEmpty() && uiState.error != null
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = { Text("Correo electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading,
                            isError = !uiState.isEmailValid
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = {
                                Text(
                                    "Contraseña (mínimo 6 caracteres)",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            enabled = !uiState.isLoading,
                            isError = !uiState.isPasswordValid
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón crear cuenta/iniciar sesión
                        Button(
                            onClick = {
                                if (uiState.isLoginMode) {
                                    viewModel.login()
                                } else {
                                    // TODO: Implementar registro
                                    // Por ahora solo permite login
                                    viewModel.login()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            enabled = !uiState.isLoading &&
                                    uiState.email.isNotEmpty() &&
                                    uiState.password.isNotEmpty() &&
                                    uiState.isEmailValid &&
                                    uiState.isPasswordValid
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(if (uiState.isLoginMode) "INICIAR SESIÓN" else "CREAR CUENTA")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Cambiar entre login y registro
                        Text(
                            text = if (uiState.isLoginMode) "¿No tienes cuenta? Crear cuenta" else "¿Ya tienes cuenta? Iniciar sesión",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.switchMode() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Efecto para navegar cuando el login sea exitoso
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    AppScaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Canasta Icon",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!uiState.isVerified) {
                        // Pantalla de verificación
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
                            text = uiState.email.ifEmpty { "tu correo electrónico" },
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
                            value = uiState.verificationCode,
                            onValueChange = viewModel::onVerificationCodeChanged,
                            label = { Text("Código de verificación") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón verificar cuenta
                        Button(
                            onClick = { /* TODO: Implementar verificación */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            enabled = !uiState.isLoading && uiState.verificationCode.isNotEmpty()
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
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
                                .clickable { /* TODO: Implementar reenvío */ }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Volver al registro",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable { viewModel.switchMode() }
                        )
                    } else {
                        // Pantalla de login/registro principal
                        Text(
                            text = if (uiState.isLoginMode) "Iniciar Sesión" else "Crear Cuenta",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Titles
                        )
                        Text(
                            text = if (uiState.isLoginMode) "Accede a tu cuenta de Canasta" else "organiza tus compras con canasta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mostrar error si existe
                        uiState.error?.let { error ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Campos de texto
                        if (!uiState.isLoginMode) {
                            OutlinedTextField(
                                value = uiState.firstName,
                                onValueChange = viewModel::onFirstNameChanged,
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoading,
                                isError = uiState.firstName.isEmpty() && uiState.error != null
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.lastName,
                                onValueChange = viewModel::onLastNameChanged,
                                label = { Text("Apellido") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoading,
                                isError = uiState.lastName.isEmpty() && uiState.error != null
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = { Text("Correo electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading,
                            isError = !uiState.isEmailValid
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = {
                                Text(
                                    "Contraseña (mínimo 6 caracteres)",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            enabled = !uiState.isLoading,
                            isError = !uiState.isPasswordValid
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón crear cuenta/iniciar sesión
                        Button(
                            onClick = {
                                if (uiState.isLoginMode) {
                                    viewModel.login()
                                } else {
                                    // TODO: Implementar registro
                                    // Por ahora solo permite login
                                    viewModel.login()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            enabled = !uiState.isLoading &&
                                    uiState.email.isNotEmpty() &&
                                    uiState.password.isNotEmpty() &&
                                    uiState.isEmailValid &&
                                    uiState.isPasswordValid
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(if (uiState.isLoginMode) "INICIAR SESIÓN" else "CREAR CUENTA")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Cambiar entre login y registro
                        Text(
                            text = if (uiState.isLoginMode) "¿No tienes cuenta? Crear cuenta" else "¿Ya tienes cuenta? Iniciar sesión",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.switchMode() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
