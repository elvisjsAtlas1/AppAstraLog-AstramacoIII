package com.example.astralog.data.repository

import com.example.astralog.data.remote.api.RetrofitProvider
import com.example.astralog.data.remote.response.PedidoResponse
import com.example.astralog.utils.Resource

class PedidoRepository {

    suspend fun obtenerMisPedidos(token: String): Resource<List<PedidoResponse>> {
        return try {
            val response = RetrofitProvider.api.getMisPedidos("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                // 🔥 EXTRAEMOS EL CONTENIDO: Pasamos la lista limpia (.content) que espera el Resource
                val listaPedidos = response.body()!!.content
                Resource.Success(listaPedidos)
            } else {
                Resource.Error("No se pudieron obtener los pedidos")
            }
        } catch (e: Exception) {
            // Esto capturará cualquier problema antes de que tumbe la app
            Resource.Error("Error de conexión: ${e.message}")
        }
    }
}