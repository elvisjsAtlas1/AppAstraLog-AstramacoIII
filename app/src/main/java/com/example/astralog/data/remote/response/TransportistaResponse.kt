package com.example.astralog.data.remote.response

data class TransportistaResponse(
    val id: Long,
    val nombre: String,
    val apellidos: String,
    val dni: String,
    val edad: Int,
    val tipoTransporte: String,
    val placa: String,
    val vehiculoInfo: String,
    val capacidad: Double,
    val estado: String,
    val documentos: List<DocumentoResponse>
)