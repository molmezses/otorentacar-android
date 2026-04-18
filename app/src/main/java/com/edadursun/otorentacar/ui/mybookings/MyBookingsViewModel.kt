package com.edadursun.otorentacar.ui.mybookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.remote.response.SearchReservationResponse
import com.edadursun.otorentacar.data.repository.ReservationQueryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// MyBookings ekranında kullanılacak UI state yapısı
sealed class MyBookingsUiState {
    object Idle : MyBookingsUiState() // İlk durum
    object Loading : MyBookingsUiState() // İstek atılırken
    data class Success(val reservation: SearchReservationResponse) : MyBookingsUiState() // Başarılı sonuç
    data class Error(val message: String) : MyBookingsUiState() // Hata mesajı
}

// Rezervasyon sorgulama ekranının ViewModel'i
class MyBookingsViewModel : ViewModel() {

    // Repository bağlantısı
    private val repository = ReservationQueryRepository(RetrofitProvider.reservationQueryApiService)

    // Ekranın state bilgisini tutar
    private val _uiState = MutableStateFlow<MyBookingsUiState>(MyBookingsUiState.Idle)
    val uiState: StateFlow<MyBookingsUiState> = _uiState

    // Rezervasyon koduna göre API'den rezervasyon arar
    fun searchReservation(reservationCode: String) {
        viewModelScope.launch {
            val token = TokenStore.token.orEmpty()

            // Token yoksa hata ver
            if (token.isBlank()) {
                _uiState.value = MyBookingsUiState.Error("Token bulunamadı.")
                return@launch
            }

            // Yükleniyor durumuna geç
            _uiState.value = MyBookingsUiState.Loading

            // Repository üzerinden sorgu yap
            val result = repository.searchReservation(
                token = token,
                reservationCode = reservationCode
            )

            // Sonuca göre state'i güncelle
            result.onSuccess { response ->
                _uiState.value = MyBookingsUiState.Success(response)
            }.onFailure { error ->
                _uiState.value = MyBookingsUiState.Error(
                    error.message ?: "Rezervasyon bulunamadı"
                )
            }
        }
    }
}