package com.example.astralog.data.remote.response

data class AuthResponse(
    val token: String,
    val username: String? = null,
    val rol: String? = null
)