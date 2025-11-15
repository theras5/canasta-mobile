package com.example.canasta.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.data.repository.AuthRepository
import com.example.canasta.data.seed.SeedingManager
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
    val isAuthenticated: Boolean = false
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
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun updateSurname(surname: String) {
        _uiState.value = _uiState.value.copy(surname = surname, errorMessage = null)
    }

    fun updateVerificationCode(code: String) {
        _uiState.value = _uiState.value.copy(verificationCode = code, errorMessage = null)
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null
        )
    }

    fun register() {
        val state = _uiState.value

        // Validaciones
        if (state.name.isBlank() || state.surname.isBlank() ||
            state.email.isBlank() || state.password.length < 6) {
            _uiState.value = state.copy(
                errorMessage = "Por favor completa todos los campos correctamente"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            repository.register(state.name, state.surname, state.email, state.password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isVerificationMode = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al registrar"
                    )
                }
        }
    }

    fun login() {
        val state = _uiState.value

        // Validaciones
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

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
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al iniciar sesión"
                    )
                }
        }
    }

    fun verifyAccount() {
        val state = _uiState.value

        if (state.verificationCode.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Ingresa el código de verificación")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            repository.verifyAccount(state.verificationCode)
                .onSuccess {
                    // Después de verificar, cambiar a modo login
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isVerificationMode = false,
                        isLoginMode = true,
                        verificationCode = "",
                        errorMessage = "Cuenta verificada. Ahora puedes iniciar sesión"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Código inválido"
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
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al reenviar código"
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
