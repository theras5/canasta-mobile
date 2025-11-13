package com.example.canasta.ui.screens.auth

import com.example.canasta.data.model.User

data class LoginUiState(
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isFetching: Boolean = false,
    val error: String? = null,
    val isLoginMode: Boolean = false,
    val isVerified: Boolean = true,
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val verificationCode: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true
)
