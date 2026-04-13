package com.example.astralog.ui.screens.pedidos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astralog.data.local.TokenManager
import com.example.astralog.data.repository.PedidoRepository
import com.example.astralog.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PedidosViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val pedidoRepository = PedidoRepository()

    private val _uiState = MutableStateFlow(PedidosUiState(isLoading = true))
    val uiState: StateFlow<PedidosUiState> = _uiState.asStateFlow()

    fun cargarMisPedidos() {
        val token = tokenManager.getToken()

        if (token.isNullOrBlank()) {
            _uiState.value = PedidosUiState(error = "Sesión no encontrada")
            return
        }

        viewModelScope.launch {
            _uiState.value = PedidosUiState(isLoading = true)

            when (val result = pedidoRepository.obtenerMisPedidos(token)) {
                is Resource.Success -> {
                    _uiState.value = PedidosUiState(
                        isLoading = false,
                        pedidos = result.data
                    )
                }

                is Resource.Error -> {
                    _uiState.value = PedidosUiState(
                        isLoading = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = PedidosUiState(isLoading = true)
                }
            }
        }
    }
}