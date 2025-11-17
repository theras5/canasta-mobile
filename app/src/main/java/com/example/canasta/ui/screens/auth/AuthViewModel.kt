package com.example.canasta.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.R
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
    val isPasswordRecoveryMode: Boolean = false,
    val isPasswordResetMode: Boolean = false,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val surname: String = "",
    val verificationCode: String = "",
    val resetCode: String = "",
    val newPassword: String = "",
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    // Errores específicos por campo
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val surnameError: String? = null,
    val verificationCodeError: String? = null,
    val resetCodeError: String? = null,
    val newPasswordError: String? = null
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

    fun updateResetCode(code: String) {
        _uiState.value = _uiState.value.copy(resetCode = code, resetCodeError = null, errorMessage = null)
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password, newPasswordError = null, errorMessage = null)
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
        val error = ValidationUtils.getEmailError(_uiState.value.email, getApplication())
        _uiState.value = _uiState.value.copy(emailError = error)
    }

    // Validar contraseña cuando el usuario sale del campo
    fun validatePassword() {
        val error = ValidationUtils.getPasswordError(_uiState.value.password, getApplication())
        _uiState.value = _uiState.value.copy(passwordError = error)
    }

    // Validar nombre cuando el usuario sale del campo
    fun validateName() {
        val context = getApplication<Application>()
        val error = ValidationUtils.getNameError(_uiState.value.name, context.getString(R.string.field_name), context)
        _uiState.value = _uiState.value.copy(nameError = error)
    }

    // Validar apellido cuando el usuario sale del campo
    fun validateSurname() {
        val context = getApplication<Application>()
        val error = ValidationUtils.getNameError(_uiState.value.surname, context.getString(R.string.field_surname), context)
        _uiState.value = _uiState.value.copy(surnameError = error)
    }

    fun register() {
        val state = _uiState.value
        val context = getApplication<Application>()

        // Validar todos los campos
        val nameError = ValidationUtils.getNameError(state.name, context.getString(R.string.field_name), context)
        val surnameError = ValidationUtils.getNameError(state.surname, context.getString(R.string.field_surname), context)
        val emailError = ValidationUtils.getEmailError(state.email, context)
        val passwordError = ValidationUtils.getPasswordError(state.password, context)

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
                            context.getString(R.string.error_email_already_registered)
                        error.message?.contains("400") == true -> context.getString(R.string.error_verify_data)
                        else -> context.getString(R.string.error_register_generic)
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
        val context = getApplication<Application>()

        // Validar campos
        val emailError = ValidationUtils.getEmailError(state.email, context)
        val passwordError = ValidationUtils.getPasswordError(state.password, context)

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
                            context.getString(R.string.error_incorrect_credentials)
                        error.message?.contains("403") == true || error.message?.contains("not verified") == true ->
                            context.getString(R.string.error_account_not_verified)
                        error.message?.contains("404") == true -> context.getString(R.string.error_incorrect_credentials)
                        error.message?.contains("400") == true -> context.getString(R.string.error_verify_data)
                        else -> context.getString(R.string.error_login_generic)
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
        val context = getApplication<Application>()

        if (state.verificationCode.isBlank()) {
            _uiState.value = state.copy(verificationCodeError = context.getString(R.string.error_verification_code_required))
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
                            val context = getApplication<Application>()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isVerificationMode = false,
                                isLoginMode = true,
                                verificationCode = "",
                                errorMessage = context.getString(R.string.account_verified_login)
                            )
                        }
                }
                .onFailure { error ->
                    // Convertir errores técnicos en mensajes amigables
                    val friendlyMessage = when {
                        error.message?.contains("400") == true -> context.getString(R.string.error_verification_code_incorrect)
                        error.message?.contains("404") == true -> context.getString(R.string.error_verification_code_incorrect)
                        error.message?.contains("expired") == true -> context.getString(R.string.error_verification_code_expired)
                        error.message?.contains("invalid") == true -> context.getString(R.string.error_verification_code_incorrect)
                        else -> context.getString(R.string.error_verification_code_incorrect)
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
            val context = getApplication<Application>()
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.resendVerification(_uiState.value.email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = context.getString(R.string.code_resent)
                    )
                }
                .onFailure { error ->
                    // Convertir errores técnicos en mensajes amigables
                    val friendlyMessage = when {
                        error.message?.contains("400") == true -> context.getString(R.string.error_resend_code_failed)
                        error.message?.contains("404") == true -> context.getString(R.string.error_user_not_found)
                        error.message?.contains("already verified") == true -> context.getString(R.string.error_account_already_verified)
                        else -> context.getString(R.string.error_resend_generic)
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

    // Password Recovery Functions
    fun startPasswordRecovery() {
        _uiState.value = _uiState.value.copy(
            isPasswordRecoveryMode = true,
            isLoginMode = false,
            isPasswordResetMode = false,
            errorMessage = null,
            emailError = null
        )
    }

    fun sendPasswordResetCode() {
        val state = _uiState.value
        val context = getApplication<Application>()

        // Validar email
        val emailError = ValidationUtils.getEmailError(state.email, context)
        if (emailError != null) {
            _uiState.value = state.copy(emailError = emailError)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessage = null,
                emailError = null
            )

            repository.sendPasswordResetCode(state.email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPasswordRecoveryMode = false,
                        isPasswordResetMode = true,
                        errorMessage = context.getString(R.string.code_sent_to_email, state.email)
                    )
                }
                .onFailure { error ->
                    val friendlyMessage = when {
                        error.message?.contains("404") == true -> context.getString(R.string.error_user_not_found)
                        else -> context.getString(R.string.error_sending_reset_code)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = friendlyMessage
                    )
                }
        }
    }

    fun resetPassword() {
        val state = _uiState.value
        val context = getApplication<Application>()

        // Validar campos
        if (state.resetCode.isBlank()) {
            _uiState.value = state.copy(resetCodeError = context.getString(R.string.error_verification_code_required))
            return
        }

        val passwordError = ValidationUtils.getPasswordError(state.newPassword, context)
        if (passwordError != null) {
            _uiState.value = state.copy(newPasswordError = passwordError)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessage = null,
                resetCodeError = null,
                newPasswordError = null
            )

            repository.resetPassword(state.resetCode, state.newPassword)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPasswordResetMode = false,
                        isPasswordRecoveryMode = false,
                        isLoginMode = true,
                        resetCode = "",
                        newPassword = "",
                        password = "",
                        errorMessage = context.getString(R.string.password_reset_success)
                    )
                }
                .onFailure { error ->
                    val friendlyMessage = when {
                        error.message?.contains("400") == true || error.message?.contains("invalid") == true ->
                            context.getString(R.string.error_invalid_reset_code)
                        error.message?.contains("404") == true -> context.getString(R.string.error_user_not_found)
                        else -> context.getString(R.string.error_reset_password_failed)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = friendlyMessage
                    )
                }
        }
    }

    fun backToLogin() {
        _uiState.value = _uiState.value.copy(
            isPasswordRecoveryMode = false,
            isPasswordResetMode = false,
            isLoginMode = true,
            resetCode = "",
            newPassword = "",
            errorMessage = null,
            emailError = null,
            resetCodeError = null,
            newPasswordError = null
        )
    }

    fun backToPasswordRecovery() {
        _uiState.value = _uiState.value.copy(
            isPasswordResetMode = false,
            isPasswordRecoveryMode = true,
            resetCode = "",
            newPassword = "",
            errorMessage = null,
            resetCodeError = null,
            newPasswordError = null
        )
    }
}
