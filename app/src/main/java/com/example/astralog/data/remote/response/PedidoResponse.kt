package com.example.astralog.data.remote.response

data class PedidoResponse(
    val id: Long,
    val clienteNombre: String,
    val clienteTelefono: String,
    val direccionEnvio: String,
    val tipoTransporte: String,
    val material: String,
    val cantidad: Double,
    val montoTotal: Double,
    val adelanto: Double,
    val piso: Int,
    val horaEnvio: String,
    val transportistaId: Long,
    val transportistaNombre: String,
    val estado: String,
    val codigoVerificacion: String
)