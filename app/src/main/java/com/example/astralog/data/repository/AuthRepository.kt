package com.example.astralog.data.repository

import com.example.astralog.data.remote.api.RetrofitProvider
import com.example.astralog.data.remote.dto.LoginRequest
import com.example.astralog.data.remote.response.AuthResponse
import com.example.astralog.utils.Resource

class AuthRepository {

    suspend fun login(username: String, password: String): Resource<AuthResponse> {
        return try {
            val response = RetrofitProvider.api.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Usuario o contraseña incorrectos")
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }
}