package com.example.astralog.ui.screens.carga

import com.example.astralog.data.remote.response.CargaResponse
import com.example.astralog.data.remote.response.TransportistaResponse

data class CargaUiState(
    val isLoading: Boolean = false,
    val transportista: TransportistaResponse? = null,
    val carga: CargaResponse? = null,
    val tipoMaterialSeleccionado: String = "PANDERETA",
    val cantidadTexto: String = "",
    val cantidadAumentarTexto: String = "",
    val error: String? = null,
    val success: String? = null
)