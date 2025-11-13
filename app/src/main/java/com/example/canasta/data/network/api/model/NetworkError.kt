package com.example.canasta.data.network.api.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkError(
    val message: String,
    val code: Int? = null,
    val details: Map<String, String>? = null
)
