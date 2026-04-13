package com.example.astralog.data.remote.response

data class DocumentoResponse(
    val id: Long,
    val tipoDocumento: String,
    val valor: String,
    val fechaEmision: String?,
    val fechaVencimiento: String?,
    val activo: Boolean
)