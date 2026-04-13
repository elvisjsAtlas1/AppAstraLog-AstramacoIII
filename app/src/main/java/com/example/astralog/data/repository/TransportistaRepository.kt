package com.example.astralog.data.repository

import com.example.astralog.data.remote.api.RetrofitProvider
import com.example.astralog.data.remote.response.TransportistaResponse
import com.example.astralog.utils.Resource

class TransportistaRepository {

    suspend fun getPerfil(token: String): Resource<TransportistaResponse> {
        return try {
            val response = RetrofitProvider.api.getTransportistaMe("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("No se pudo obtener el perfil del transportista")
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }
}