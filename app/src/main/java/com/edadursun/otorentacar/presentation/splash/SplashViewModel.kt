package com.edadursun.otorentacar.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.data.repository.AuthRepository
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.SessionManager
import com.edadursun.otorentacar.core.session.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SplashUiState {
    object Idle : SplashUiState()
    object Loading : SplashUiState()
    object Success : SplashUiState()
    data class Error(val message: String) : SplashUiState()
}

class SplashViewModel : ViewModel() {

    private val authRepository = AuthRepository(RetrofitProvider.authApiService)
    private val sessionManager = SessionManager(authRepository)

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState: StateFlow<SplashUiState> = _uiState

    fun initializeSession(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading

            val result = sessionManager.initializeSession(
                username = username,
                password = password
            )

            result.onSuccess {
                _uiState.value = SplashUiState.Success
            }.onFailure { error ->
                _uiState.value =
                    SplashUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun currentToken(): String? {
        return TokenStore.token
    }
}