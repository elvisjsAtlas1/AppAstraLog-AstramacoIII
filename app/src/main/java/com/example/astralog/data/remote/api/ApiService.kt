package com.example.astralog.data.remote.api

import com.example.astralog.data.remote.dto.AumentarCargaRequest
import com.example.astralog.data.remote.dto.CargaRequest
import com.example.astralog.data.remote.dto.LoginRequest
import com.example.astralog.data.remote.response.AuthResponse
import com.example.astralog.data.remote.response.CargaResponse
import com.example.astralog.data.remote.response.PedidoResponse
import com.example.astralog.data.remote.response.SpringPageResponse
import com.example.astralog.data.remote.response.TransportistaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("transportistas/me")
    suspend fun getTransportistaMe(
        @Header("Authorization") token: String
    ): Response<TransportistaResponse>

    @GET("cargas/{transportistaId}")
    suspend fun getCargaByTransportista(
        @Path("transportistaId") transportistaId: Long,
        @Header("Authorization") token: String
    ): Response<CargaResponse?>

    // 🔥 CORRECCIÓN AQUÍ: Cambiamos de @PUT a @POST para la creación de la carga inicial
    @POST("cargas/{transportistaId}")
    suspend fun subirCargaActual(
        @Path("transportistaId") transportistaId: Long,
        @Body request: CargaRequest,
        @Header("Authorization") token: String
    ): Response<CargaResponse>

    @POST("cargas/{transportistaId}/aumentar")
    suspend fun aumentarCargaActual(
        @Path("transportistaId") transportistaId: Long,
        @Body request: AumentarCargaRequest,
        @Header("Authorization") token: String
    ): Response<CargaResponse>

    @GET("pedidos/me")
    suspend fun getMisPedidos(
        @Header("Authorization") token: String
    ): Response<SpringPageResponse<PedidoResponse>>
}