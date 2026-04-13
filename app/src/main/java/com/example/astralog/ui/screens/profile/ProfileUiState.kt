package com.example.astralog.ui.screens.profile

import com.example.astralog.data.remote.response.TransportistaResponse

data class ProfileUiState(
    val isLoading: Boolean = false,
    val transportista: TransportistaResponse? = null,
    val error: String? = null
)