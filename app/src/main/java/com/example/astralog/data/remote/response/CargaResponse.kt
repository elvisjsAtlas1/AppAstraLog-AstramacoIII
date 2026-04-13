package com.example.astralog.data.remote.response

data class CargaResponse(
    val id: Long,
    val transportistaId: Long,
    val transportistaNombre: String,
    val tipoMaterial: String,
    val cantidadDisponible: Double
)