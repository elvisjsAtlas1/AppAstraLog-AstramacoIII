package com.example.astralog.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astralog.data.local.TokenManager
import com.example.astralog.data.repository.TransportistaRepository
import com.example.astralog.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransportistaRepository()
    private val tokenManager = TokenManager(application)

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            _uiState.value = ProfileUiState(error = "Sesión no encontrada")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState(isLoading = true)

            when (val result = repository.getPerfil(token)) {
                is Resource.Success -> {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        transportista = result.data
                    )
                }

                is Resource.Error -> {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = ProfileUiState(isLoading = true)
                }
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}