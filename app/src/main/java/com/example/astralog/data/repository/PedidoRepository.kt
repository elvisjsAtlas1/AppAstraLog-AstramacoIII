package com.example.astralog.data.repository

import com.example.astralog.data.remote.api.RetrofitProvider
import com.example.astralog.data.remote.response.PedidoResponse
import com.example.astralog.utils.Resource

class PedidoRepository {

    suspend fun obtenerMisPedidos(token: String): Resource<List<PedidoResponse>> {
        return try {
            val response = RetrofitProvider.api.getMisPedidos("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("No se pudieron obtener los pedidos")
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }
}