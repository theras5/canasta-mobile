package com.example.canasta.data.repository

import com.example.canasta.data.DataSourceException
import com.example.canasta.data.model.User
import com.example.canasta.data.network.UserRemoteDataSource
import com.example.canasta.data.network.model.NetworkCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {

    private val remoteDataSource = UserRemoteDataSource()
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        // Configurar el token provider en RetrofitClient
        com.example.canasta.data.network.api.RetrofitClient.tokenProvider = {
            _currentUser.value?.token
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val credentials = NetworkCredentials(email, password)
            val response = remoteDataSource.login(credentials)

            if (response.isSuccessful) {
                response.body()?.let { networkToken ->
                    val user = User(
                        email = email,
                        token = networkToken.token
                    )
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    Result.success(user)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Invalid email or password"
                    400 -> "Invalid request format"
                    500 -> "Server error"
                    else -> "Unknown error: ${response.code()}"
                }
                Result.failure(DataSourceException(response.code(), errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(DataSourceException(message = e.message ?: "Network error"))
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = remoteDataSource.logout()

            if (response.isSuccessful) {
                _currentUser.value = null
                _isLoggedIn.value = false
                Result.success(Unit)
            } else {
                // Even if logout fails on server, clear local state
                _currentUser.value = null
                _isLoggedIn.value = false
                Result.success(Unit)
            }
        } catch (e: Exception) {
            // Clear local state even on network error
            _currentUser.value = null
            _isLoggedIn.value = false
            Result.success(Unit)
        }
    }

    fun clearUserData() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository().also { INSTANCE = it }
            }
        }
    }
}