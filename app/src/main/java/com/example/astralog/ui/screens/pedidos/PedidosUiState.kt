package com.example.astralog.ui.screens.pedidos

import com.example.astralog.data.remote.response.PedidoResponse

data class PedidosUiState(
    val isLoading: Boolean = false,
    val pedidos: List<PedidoResponse> = emptyList(),
    val error: String? = null
)