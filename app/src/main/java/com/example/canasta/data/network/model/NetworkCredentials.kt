package com.example.canasta.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkCredentials(
    val email: String,
    val password: String
)
