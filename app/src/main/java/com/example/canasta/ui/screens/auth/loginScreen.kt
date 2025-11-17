package com.example.canasta.ui.screens.auth

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
                            verificationCodeError = uiState.verificationCodeError,
                            isLoading = uiState.isLoading,
                            keyboardController = keyboardController,
                            focusManager = focusManager
                        )
                    } else if (uiState.isPasswordRecoveryMode) {
                        PasswordRecoveryContent(
                            email = uiState.email,
                            onEmailChange = viewModel::updateEmail,
                            onSendCode = viewModel::sendPasswordResetCode,
                            onCancel = viewModel::backToLogin,
                            errorMessage = uiState.errorMessage,
                            emailError = uiState.emailError,
                            isLoading = uiState.isLoading,
                            keyboardController = keyboardController,
                            focusManager = focusManager
                        )
                    } else if (uiState.isPasswordResetMode) {
                        PasswordResetContent(
                            email = uiState.email,
                            resetCode = uiState.resetCode,
                            newPassword = uiState.newPassword,
                            onResetCodeChange = viewModel::updateResetCode,
                            onNewPasswordChange = viewModel::updateNewPassword,
                            onResetPassword = viewModel::resetPassword,
                            onBack = viewModel::backToPasswordRecovery,
                            onCancel = viewModel::backToLogin,
                            errorMessage = uiState.errorMessage,
                            resetCodeError = uiState.resetCodeError,
                            newPasswordError = uiState.newPasswordError,
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
                            onForgotPassword = viewModel::startPasswordRecovery,
                            errorMessage = uiState.errorMessage,
                            emailError = uiState.emailError,
                            passwordError = uiState.passwordError,
                            nameError = uiState.nameError,
                            surnameError = uiState.surnameError,
                            onValidateEmail = viewModel::validateEmail,
                            onValidatePassword = viewModel::validatePassword,
                            onValidateName = viewModel::validateName,
                            onValidateSurname = viewModel::validateSurname,
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
    verificationCodeError: String?,
    isLoading: Boolean,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    Text(
        text = stringResource(R.string.verify_your_account),
        style = MaterialTheme.typography.headlineMedium,
        color = Titles,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.verification_code_sent_to),
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
        text = stringResource(R.string.enter_code_to_activate),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))

    // Campo de código de verificación
    Column {
        OutlinedTextField(
            value = verificationCode,
            onValueChange = onCodeChange,
            label = { Text(stringResource(R.string.verification_code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            isError = verificationCodeError != null,
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

        if (verificationCodeError != null) {
            Text(
                text = verificationCodeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

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
            Text(stringResource(R.string.verify_account_button))
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Enlaces de verificación
    Text(
        text = stringResource(R.string.didnt_receive_code),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Text(
        text = stringResource(R.string.resend_code),
        style = MaterialTheme.typography.bodySmall,
        color = Secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable(enabled = !isLoading) { onResend() }
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.back_to_register),
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
    onForgotPassword: () -> Unit,
    errorMessage: String?,
    emailError: String?,
    passwordError: String?,
    nameError: String?,
    surnameError: String?,
    onValidateEmail: () -> Unit,
    onValidatePassword: () -> Unit,
    onValidateName: () -> Unit,
    onValidateSurname: () -> Unit,
    isLoading: Boolean,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Text(
        text = if (isLoginMode) stringResource(R.string.login) else stringResource(R.string.create_account),
        style = MaterialTheme.typography.headlineMedium,
        color = Titles
    )
    Text(
        text = if (isLoginMode) stringResource(R.string.access_your_canasta_account) else stringResource(R.string.organize_your_shopping),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = 4.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Campos de texto
    if (!isLoginMode) {
        Column {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                enabled = !isLoading,
                isError = nameError != null,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onValidateName() }
                ),
                singleLine = true
            )
            if (nameError != null) {
                Text(
                    text = nameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Column {
            OutlinedTextField(
                value = surname,
                onValueChange = onSurnameChange,
                label = { Text(stringResource(R.string.surname_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = !isLoading,
                isError = surnameError != null,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onValidateSurname() }
                ),
                singleLine = true
            )
            if (surnameError != null) {
                Text(
                    text = surnameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = !isLoading,
            isError = emailError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { onValidateEmail() }
            ),
            singleLine = true
        )
        if (emailError != null) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    Column {
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text(
                    stringResource(R.string.password_label),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            stringResource(R.string.hide_password)
                        else
                            stringResource(R.string.show_password),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            enabled = !isLoading,
            isError = passwordError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onValidatePassword()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            singleLine = true
        )
        if (passwordError != null) {
            Text(
                text = passwordError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

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
            Text(if (isLoginMode) stringResource(R.string.login_button) else stringResource(R.string.create_account_button))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    // Links de navegación
    if (isLoginMode) {
        Text(
            text = stringResource(R.string.dont_have_account),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.bodySmall,
            color = Secondary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(enabled = !isLoading) { onToggleMode() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.forgot_password),
            style = MaterialTheme.typography.bodySmall,
            color = Secondary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(enabled = !isLoading) { onForgotPassword() }
        )
    } else {
        Text(
            text = stringResource(R.string.already_have_account),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.login_link),
            style = MaterialTheme.typography.bodySmall,
            color = Secondary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(enabled = !isLoading) { onToggleMode() }
        )
    }
}

@Composable
private fun PasswordRecoveryContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onCancel: () -> Unit,
    errorMessage: String?,
    emailError: String?,
    isLoading: Boolean,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    Text(
        text = stringResource(R.string.recover_password_title),
        style = MaterialTheme.typography.headlineMedium,
        color = Titles,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.recover_password_message),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))

    // Campo de email
    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            isError = emailError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
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

        if (emailError != null) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Botones
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        ) {
            Text(stringResource(R.string.cancel))
        }

        Button(
            onClick = onSendCode,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = !isLoading,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.send_code_button),
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PasswordResetContent(
    email: String,
    resetCode: String,
    newPassword: String,
    onResetCodeChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onResetPassword: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    errorMessage: String?,
    resetCodeError: String?,
    newPasswordError: String?,
    isLoading: Boolean,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Text(
        text = stringResource(R.string.reset_password_title),
        style = MaterialTheme.typography.headlineMedium,
        color = Titles,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Mensaje de éxito o info
    if (errorMessage != null && errorMessage.contains("enviado")) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Campo de código de verificación
    Column {
        OutlinedTextField(
            value = resetCode,
            onValueChange = onResetCodeChange,
            label = { Text(stringResource(R.string.reset_code_label)) },
            placeholder = { Text(stringResource(R.string.reset_code_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            isError = resetCodeError != null,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        if (resetCodeError != null) {
            Text(
                text = resetCodeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Campo de nueva contraseña
    Column {
        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text(stringResource(R.string.reset_password_label)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            stringResource(R.string.hide_password)
                        else
                            stringResource(R.string.show_password),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            enabled = !isLoading,
            isError = newPasswordError != null,
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

        if (newPasswordError != null) {
            Text(
                text = newPasswordError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    if (errorMessage != null && !errorMessage.contains("enviado")) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Botones
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onResetPassword,
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
                Text(stringResource(R.string.reset_password_button))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.back_button))
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}

