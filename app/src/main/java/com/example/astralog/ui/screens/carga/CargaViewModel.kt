package com.example.astralog.ui.screens.carga

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astralog.data.local.TokenManager
import com.example.astralog.data.repository.CargaRepository
import com.example.astralog.data.repository.TransportistaRepository
import com.example.astralog.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CargaViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val transportistaRepository = TransportistaRepository()
    private val cargaRepository = CargaRepository()

    private val _uiState = MutableStateFlow(CargaUiState(isLoading = true))
    val uiState: StateFlow<CargaUiState> = _uiState.asStateFlow()

    fun onTipoMaterialChange(value: String) {
        _uiState.value = _uiState.value.copy(
            tipoMaterialSeleccionado = value,
            error = null,
            success = null
        )
    }

    fun onCantidadChange(value: String) {
        _uiState.value = _uiState.value.copy(
            cantidadTexto = value,
            error = null,
            success = null
        )
    }

    fun onCantidadAumentarChange(value: String) {
        _uiState.value = _uiState.value.copy(
            cantidadAumentarTexto = value,
            error = null,
            success = null
        )
    }

    fun cargarDatos() {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            _uiState.value = CargaUiState(error = "Sesión no encontrada")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, success = null)

            when (val perfilResult = transportistaRepository.getPerfil(token)) {
                is Resource.Success -> {
                    val transportista = perfilResult.data

                    if (transportista.tipoTransporte != "CAMIONERO") {
                        _uiState.value = CargaUiState(
                            isLoading = false,
                            transportista = transportista,
                            error = "Solo los transportistas CAMIONERO manejan carga"
                        )
                        return@launch
                    }

                    when (val cargaResult = cargaRepository.obtenerCarga(token, transportista.id)) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                transportista = transportista,
                                carga = cargaResult.data,
                                tipoMaterialSeleccionado = cargaResult.data?.tipoMaterial ?: "PANDERETA"
                            )
                        }

                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                transportista = transportista,
                                carga = null,
                                error = cargaResult.message
                            )
                        }

                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                    }
                }

                is Resource.Error -> {
                    _uiState.value = CargaUiState(
                        isLoading = false,
                        error = perfilResult.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun subirCargaActual() {
        val state = _uiState.value
        val token = tokenManager.getToken()
        val transportistaId = state.transportista?.id

        if (token.isNullOrBlank() || transportistaId == null) {
            _uiState.value = state.copy(error = "No se pudo identificar la sesión")
            return
        }

        val cantidad = state.cantidadTexto.toDoubleOrNull()
        if (cantidad == null || cantidad < 0) {
            _uiState.value = state.copy(error = "Ingrese una cantidad válida")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null, success = null)

            when (
                val result = cargaRepository.subirCargaActual(
                    token = token,
                    transportistaId = transportistaId,
                    tipoMaterial = state.tipoMaterialSeleccionado,
                    cantidadDisponible = cantidad
                )
            ) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        carga = result.data,
                        cantidadTexto = "",
                        success = "Carga actual guardada correctamente"
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun aumentarCargaActual() {
        val state = _uiState.value
        val token = tokenManager.getToken()
        val transportistaId = state.transportista?.id

        if (token.isNullOrBlank() || transportistaId == null) {
            _uiState.value = state.copy(error = "No se pudo identificar la sesión")
            return
        }

        val cantidad = state.cantidadAumentarTexto.toDoubleOrNull()
        if (cantidad == null || cantidad <= 0) {
            _uiState.value = state.copy(error = "Ingrese una cantidad mayor a cero")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null, success = null)

            when (
                val result = cargaRepository.aumentarCargaActual(
                    token = token,
                    transportistaId = transportistaId,
                    tipoMaterial = state.tipoMaterialSeleccionado,
                    cantidadAgregar = cantidad
                )
            ) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        carga = result.data,
                        cantidadAumentarTexto = "",
                        success = "Carga aumentada correctamente"
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
}