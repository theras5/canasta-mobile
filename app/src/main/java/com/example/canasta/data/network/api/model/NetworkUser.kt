package com.example.canasta.data.network.api.model

import com.example.canasta.data.model.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class NetworkUser(
    var id: Int,
    var email: String?,
    var firstName: String?,
    var lastName: String?,
    var avatar: String?,
    var isEmailVerified: Boolean?,
    @Contextual
    var createdAt: Date? = null,
    @Contextual
    var updatedAt: Date? = null
) {
    fun asModel(): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            avatar = avatar,
            isEmailVerified = isEmailVerified,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

@Serializable
data class NetworkNewUser(
    var email: String?,
    var firstName: String?,
    var lastName: String?,
    var password: String? = null
)

@Serializable
data class NetworkLoginRequest(
    var email: String,
    var password: String
)

@Serializable
data class NetworkRegisterRequest(
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String
)

@Serializable
data class NetworkAuthResponse(
    var user: NetworkUser,
    var token: String,
    var refreshToken: String? = null
)

@Serializable
data class NetworkVerificationRequest(
    var email: String,
    var verificationCode: String
)
