package com.edadursun.otorentacar.ui.reservations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.data.model.ReservationUiModel
import com.edadursun.otorentacar.data.remote.response.SearchReservationResponse
import com.edadursun.otorentacar.data.repository.ReservationQueryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

sealed class ReservationsUiState {
    object Idle : ReservationsUiState()
    object Loading : ReservationsUiState()
    data class Success(val reservations: List<ReservationUiModel>) : ReservationsUiState()
    data class Error(val message: String) : ReservationsUiState()
}

// Rezervasyon kodlarına göre backend'den rezervasyonları çekip UI'a hazır hale getirir
class ReservationsViewModel(
    private val repository: ReservationQueryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReservationsUiState>(ReservationsUiState.Idle)
    val uiState: StateFlow<ReservationsUiState> = _uiState

    // Kayıtlı rezervasyon kodlarını kullanarak tüm rezervasyonları yükler
    fun loadReservations(
        token: String,
        reservationCodes: List<String>
    ) {
        // Kayıtlı kod yoksa boş liste döndür
        if (reservationCodes.isEmpty()) {
            _uiState.value = ReservationsUiState.Success(emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = ReservationsUiState.Loading

            try {
                // Her rezervasyon kodu için ayrı sorgu yap
                val results = reservationCodes.map { code ->
                    async {
                        repository.searchReservation(
                            token = token,
                            reservationCode = code
                        )
                    }
                }.awaitAll()

                // Başarılı sonuçları UI modeline çevir
                val reservations = results.mapNotNull { result ->
                    result.getOrNull()?.let { response ->
                        mapToUiModel(response)
                    }
                }

                _uiState.value = ReservationsUiState.Success(reservations)
            } catch (e: Exception) {
                _uiState.value = ReservationsUiState.Error(
                    e.message ?: "Rezervasyonlar yüklenemedi"
                )
            }
        }
    }

    // API'den gelen rezervasyon verisini kartta gösterilecek sade modele çevirir
    private fun mapToUiModel(response: SearchReservationResponse): ReservationUiModel {
        val reservation = response.`object`

        return ReservationUiModel(
            reservationCode = reservation.reservationCode,
            vehicleName = reservation.vehicleModel.name,
            statusText = reservation.status.name,
            pickupLocation = "Alış: ${reservation.pickUpLocationPoint.name}",
            dropOffLocation = "Dönüş: ${reservation.dropOffLocationPoint.name}",
            dateRangeText = "${formatReservationDateTime(reservation.pickUpDateTime)} - ${formatReservationDateTime(reservation.dropOffDateTime)}",
            totalPriceText = "${getCurrencySymbol(reservation.currency.code)}${reservation.totalPrice}"        )
    }

    private fun formatReservationDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr"))
            val date = inputFormat.parse(dateTime)
            if (date != null) outputFormat.format(date) else dateTime
        } catch (e: Exception) {
            dateTime
        }
    }

    private fun getCurrencySymbol(code: String): String {
        return when (code.uppercase()) {
            "EUR" -> "€"
            "TRY" -> "₺"
            "USD" -> "$"
            else -> code
        }
    }
}