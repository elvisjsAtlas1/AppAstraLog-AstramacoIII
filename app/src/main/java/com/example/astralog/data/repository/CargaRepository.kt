package com.example.astralog.data.repository

import com.example.astralog.data.remote.api.RetrofitProvider
import com.example.astralog.data.remote.dto.AumentarCargaRequest
import com.example.astralog.data.remote.dto.CargaRequest
import com.example.astralog.data.remote.response.CargaResponse
import com.example.astralog.utils.Resource

class CargaRepository {

    suspend fun obtenerCarga(token: String, transportistaId: Long): Resource<CargaResponse?> {
        return try {
            val response = RetrofitProvider.api.getCargaByTransportista(
                transportistaId = transportistaId,
                token = "Bearer $token"
            )

            when {
                response.isSuccessful -> {
                    Resource.Success(response.body())
                }
                response.code() == 404 -> {
                    Resource.Success(null)
                }
                else -> {
                    Resource.Error("No se pudo obtener la carga actual")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun subirCargaActual(
        token: String,
        transportistaId: Long,
        tipoMaterial: String,
        cantidadDisponible: Double
    ): Resource<CargaResponse> {
        return try {
            val response = RetrofitProvider.api.subirCargaActual(
                transportistaId = transportistaId,
                request = CargaRequest(tipoMaterial, cantidadDisponible),
                token = "Bearer $token"
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("No se pudo guardar la carga actual")
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun aumentarCargaActual(
        token: String,
        transportistaId: Long,
        tipoMaterial: String,
        cantidadAgregar: Double
    ): Resource<CargaResponse> {
        return try {
            val response = RetrofitProvider.api.aumentarCargaActual(
                transportistaId = transportistaId,
                request = AumentarCargaRequest(tipoMaterial, cantidadAgregar),
                token = "Bearer $token"
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("No se pudo aumentar la carga actual")
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }
}