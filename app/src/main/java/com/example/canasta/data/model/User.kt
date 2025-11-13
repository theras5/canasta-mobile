package com.example.canasta.data.model

data class User(
    val id: Long? = null,
    val email: String,
    val name: String? = null,
    val surname: String? = null,
    val token: String? = null
)
