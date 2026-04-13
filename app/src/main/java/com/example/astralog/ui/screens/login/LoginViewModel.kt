package com.example.astralog.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astralog.data.local.TokenManager
import com.example.astralog.data.repository.AuthRepository
import com.example.astralog.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val tokenManager = TokenManager(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.username.isBlank()) {
            _uiState.value = state.copy(error = "Ingrese su usuario")
            return
        }

        if (state.password.isBlank()) {
            _uiState.value = state.copy(error = "Ingrese su contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            when (val result = repository.login(state.username.trim(), state.password.trim())) {
                is Resource.Success -> {
                    val token = result.data.token
                    if (token.isNotBlank()) {
                        tokenManager.saveToken(token)
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onSuccess()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Token inválido"
                        )
                    }
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