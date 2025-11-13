package com.example.canasta.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository = UserRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Combinar el estado del repositorio con el estado de la UI
        viewModelScope.launch {
            combine(
                userRepository.currentUser,
                userRepository.isLoggedIn
            ) { currentUser, isLoggedIn ->
                _uiState.value = _uiState.value.copy(
                    user = currentUser,
                    isLoggedIn = isLoggedIn,
                    isLoading = false
                )
            }.collect {}
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            isEmailValid = isValidEmail(email),
            error = null
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            isPasswordValid = isValidPassword(password),
            error = null
        )
    }

    fun onFirstNameChanged(firstName: String) {
        _uiState.value = _uiState.value.copy(
            firstName = firstName,
            error = null
        )
    }

    fun onLastNameChanged(lastName: String) {
        _uiState.value = _uiState.value.copy(
            lastName = lastName,
            error = null
        )
    }

    fun onVerificationCodeChanged(code: String) {
        _uiState.value = _uiState.value.copy(
            verificationCode = code,
            error = null
        )
    }

    fun switchMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            error = null,
            email = "",
            password = "",
            firstName = "",
            lastName = "",
            verificationCode = ""
        )
    }

    fun login() {
        if (!isValidInput()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = userRepository.login(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            result.fold(
                onSuccess = {
                    // El estado se actualiza automáticamente a través del combine en init
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error desconocido durante el login"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = userRepository.logout()

            result.fold(
                onSuccess = {
                    // El estado se actualiza automáticamente a través del combine en init
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = {
                    // Incluso si falla, limpiar el estado local
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun isValidInput(): Boolean {
        val currentState = _uiState.value

        if (!currentState.isLoginMode) {
            // Modo registro - validar todos los campos
            if (currentState.firstName.isBlank() || currentState.lastName.isBlank()) {
                _uiState.value = currentState.copy(
                    error = "Nombre y apellido son obligatorios"
                )
                return false
            }
        }

        // Validar email y password para ambos modos
        if (!isValidEmail(currentState.email)) {
            _uiState.value = currentState.copy(
                error = "Email inválido",
                isEmailValid = false
            )
            return false
        }

        if (!isValidPassword(currentState.password)) {
            _uiState.value = currentState.copy(
                error = "La contraseña debe tener al menos 6 caracteres",
                isPasswordValid = false
            )
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}
