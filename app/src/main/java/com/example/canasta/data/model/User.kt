package com.example.canasta.data.model

import com.example.canasta.data.network.api.model.NetworkNewUser
import com.example.canasta.data.network.api.model.NetworkUser
import java.util.*

class User(
    var id: Int?,
    var email: String?,
    var firstName: String?,
    var lastName: String?,
    var avatar: String?,
    var isEmailVerified: Boolean?,
    var createdAt: Date?,
    var updatedAt: Date?
) {
    // Convenience constructors
    constructor(id: Int) : this(id, null, null, null, null, null, null, null)

    constructor(
        email: String,
        firstName: String,
        lastName: String
    ) : this(null, email, firstName, lastName, null, false, null, null)

    // Conversion methods
    fun asNetworkNewModel(): NetworkNewUser {
        return NetworkNewUser(
            email = email,
            firstName = firstName,
            lastName = lastName
        )
    }

    fun asNetworkModel(): NetworkUser {
        return NetworkUser(
            id = id!!,
            email = email,
            firstName = firstName,
            lastName = lastName,
            avatar = avatar,
            isEmailVerified = isEmailVerified,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    val fullName: String
        get() = "$firstName $lastName".trim()
}
