package com.edadursun.otorentacar.ui.reservationdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.data.remote.request.AddReservationRequest
import com.edadursun.otorentacar.data.remote.response.AddReservationResponse
import com.edadursun.otorentacar.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReservationDetailUiState {
    object Idle : ReservationDetailUiState()
    object Loading : ReservationDetailUiState()
    data class Success(val response: AddReservationResponse) : ReservationDetailUiState()
    data class Error(val message: String) : ReservationDetailUiState()
}

class ReservationDetailViewModel : ViewModel() {

    private val repository = ReservationRepository(RetrofitProvider.reservationApiService)

    private val _uiState =
        MutableStateFlow<ReservationDetailUiState>(ReservationDetailUiState.Idle)
    val uiState: StateFlow<ReservationDetailUiState> = _uiState

    fun addReservation(request: AddReservationRequest) {
        viewModelScope.launch {
            _uiState.value = ReservationDetailUiState.Loading

            val result = repository.addReservation(request)

            result.onSuccess { response ->
                _uiState.value = ReservationDetailUiState.Success(response)
            }.onFailure { error ->
                _uiState.value = ReservationDetailUiState.Error(
                    error.message ?: "Rezervasyon oluşturulamadı"
                )
            }
        }
    }
}