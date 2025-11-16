package com.example.canasta.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.repository.AuthRepository
import com.example.canasta.data.seed.SeedingManager
import com.example.canasta.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de UI para autenticación
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoginMode: Boolean = false,
    val isVerificationMode: Boolean = false,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val surname: String = "",
    val verificationCode: String = "",
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    // Errores específicos por campo
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val surnameError: String? = null,
    val verificationCodeError: String? = null
)

/**
 * ViewModel para gestionar el estado de autenticación
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)
    private val seedingManager = SeedingManager(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null, errorMessage = null)
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, nameError = null, errorMessage = null)
    }

    fun updateSurname(surname: String) {
        _uiState.value = _uiState.value.copy(surname = surname, surnameError = null, errorMessage = null)
    }

    fun updateVerificationCode(code: String) {
        _uiState.value = _uiState.value.copy(verificationCode = code, verificationCodeError = null, errorMessage = null)
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null,
            emailError = null,
            passwordError = null,
            nameError = null,
            surnameError = null
        )
    }

    // Validar email cuando el usuario sale del campo
    fun validateEmail() {
        val error = ValidationUtils.getEmailError(_uiState.value.email)
        _uiState.value = _uiState.value.copy(emailError = error)
    }

    // Validar contraseña cuando el usuario sale del campo
    fun validatePassword() {
        val error = ValidationUtils.getPasswordError(_uiState.value.password)
        _uiState.value = _uiState.value.copy(passwordError = error)
    }

    // Validar nombre cuando el usuario sale del campo
    fun validateName() {
        val error = ValidationUtils.getNameError(_uiState.value.name, "nombre")
        _uiState.value = _uiState.value.copy(nameError = error)
    }

    // Validar apellido cuando el usuario sale del campo
    fun validateSurname() {
        val error = ValidationUtils.getNameError(_uiState.value.surname, "apellido")
        _uiState.value = _uiState.value.copy(surnameError = error)
    }

    fun register() {
        val state = _uiState.value

        // Validar todos los campos
        val nameError = ValidationUtils.getNameError(state.name, "nombre")
        val surnameError = ValidationUtils.getNameError(state.surname, "apellido")
        val emailError = ValidationUtils.getEmailError(state.email)
        val passwordError = ValidationUtils.getPasswordError(state.password)

        // Si hay algún error, mostrarlos
        if (nameError != null || surnameError != null || emailError != null || passwordError != null) {
            _uiState.value = state.copy(
                nameError = nameError,
                surnameError = surnameError,
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessage = null,
                nameError = null,
                surnameError = null,
                emailError = null,
                passwordError = null
            )

            repository.register(state.name, state.surname, state.email, state.password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isVerificationMode = true
                    )
                }
                .onFailure { error ->
                    // Convertir errores técnicos en mensajes amigables
                    val friendlyMessage = when {
                        error.message?.contains("409") == true || error.message?.contains("already exists") == true ->
                            "Este correo electrónico ya está registrado"
                        error.message?.contains("400") == true -> "Por favor verifica los datos ingresados"
                        else -> "Error al registrar. Intenta nuevamente"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = friendlyMessage
                    )
                }
        }
    }

    fun login() {
        val state = _uiState.value

        // Validar campos
        val emailError = ValidationUtils.getEmailError(state.email)
        val passwordError = ValidationUtils.getPasswordError(state.password)

        // Si hay algún error, mostrarlos
        if (emailError != null || passwordError != null) {
            _uiState.value = state.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessage = null,
                emailError = null,
                passwordError = null
            )

            repository.login(state.email, state.password)
                .onSuccess { authToken ->
                    // Ejecutamos seeding en background sin bloquear la UI; requiere token ya configurado
                    viewModelScope.launch {
                        seedingManager.seedDefaultsIfEmpty(state.email)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
                .onFailure { error ->
                    // Convertir errores técnicos en mensajes amigables
                    val friendlyMessage = when {
                        error.message?.contains("401") == true || error.message?.contains("Unauthorized") == true ->
                            "Correo o contraseña incorrectos"
                        error.message?.contains("403") == true || error.message?.contains("not verified") == true ->
                            "Debes verificar tu cuenta antes de iniciar sesión"
                        error.message?.contains("404") == true -> "Correo o contraseña incorrectos"
                        error.message?.contains("400") == true -> "Por favor verifica tus credenciales"
                        else -> "Error al iniciar sesión. Intenta nuevamente"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = friendlyMessage
                    )
                }
        }
    }

    fun verifyAccount() {
        val state = _uiState.value

        if (state.verificationCode.isBlank()) {
            _uiState.value = state.copy(verificationCodeError = "Ingresa el código de verificación")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null, verificationCodeError = null)

            repository.verifyAccount(state.verificationCode)
                .onSuccess {
                    // Después de verificar, hacer login automáticamente
                    repository.login(state.email, state.password)
                        .onSuccess { authToken ->
                            // Ejecutamos seeding en background
                            viewModelScope.launch {
                                seedingManager.seedDefaultsIfEmpty(state.email)
                            }

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isAuthenticated = true,
                                isVerificationMode = false,
                                verificationCode = ""
                            )
                        }
                        .onFailure { error ->
                            // Si falla el login automático, enviar a pantalla de login
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isVerificationMode = false,
                                isLoginMode = true,
                                verificationCode = "",
                                errorMessage = "Cuenta verificada. Por favor, inicia sesión"
                            )
                        }
                }
                .onFailure { error ->
                    // Convertir errores técnicos en mensajes amigables
                    val friendlyMessage = when {
                        error.message?.contains("400") == true -> "El código ingresado es incorrecto"
                        error.message?.contains("404") == true -> "El código ingresado es incorrecto"
                        error.message?.contains("expired") == true -> "El código ha expirado. Solicita uno nuevo"
                        error.message?.contains("invalid") == true -> "El código ingresado es incorrecto"
                        else -> "El código ingresado es incorrecto"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        verificationCodeError = friendlyMessage
                    )
                }
        }
    }

    fun resendVerification() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.resendVerification(_uiState.value.email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Código reenviado"
                    )
                }
                .onFailure { error ->
                    // Convertir errores técnicos en mensajes amigables
                    val friendlyMessage = when {
                        error.message?.contains("400") == true -> "No se pudo reenviar el código"
                        error.message?.contains("404") == true -> "Usuario no encontrado"
                        error.message?.contains("already verified") == true -> "La cuenta ya está verificada"
                        else -> "Error al reenviar código. Intenta nuevamente"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = friendlyMessage
                    )
                }
        }
    }

    fun backToRegister() {
        _uiState.value = _uiState.value.copy(
            isVerificationMode = false,
            isLoginMode = false,
            verificationCode = "",
            errorMessage = null
        )
    }
}
