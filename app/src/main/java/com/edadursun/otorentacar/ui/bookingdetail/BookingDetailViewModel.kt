package com.edadursun.otorentacar.ui.bookingdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.remote.response.PriceVehicleItemResponse
import com.edadursun.otorentacar.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

sealed class BookingDetailImageUiState {
    object Idle : BookingDetailImageUiState()
    object Loading : BookingDetailImageUiState()
    data class Success(val imageUrl: String) : BookingDetailImageUiState()
    object Empty : BookingDetailImageUiState()
    data class Error(val message: String) : BookingDetailImageUiState()
}

class BookingDetailViewModel : ViewModel() {

    private val vehicleRepository = VehicleRepository(RetrofitProvider.vehicleApiService)

    private val _imageUiState =
        MutableStateFlow<BookingDetailImageUiState>(BookingDetailImageUiState.Idle)
    val imageUiState: StateFlow<BookingDetailImageUiState> = _imageUiState

    fun fetchVehicleImage(
        vehicleModelId: Int,
        pickupLocationId: Int,
        dropOffLocationId: Int,
        rawPickupDateTime: String,
        rawDropOffDateTime: String
    ) {
        viewModelScope.launch {
            _imageUiState.value = BookingDetailImageUiState.Loading

            val token = TokenStore.token
            if (token.isNullOrBlank()) {
                _imageUiState.value = BookingDetailImageUiState.Error("Token bulunamadı")
                return@launch
            }

            val pickUpDateTime = formatSearchReservationDateForApi(rawPickupDateTime)
            val dropOffDateTime = formatSearchReservationDateForApi(rawDropOffDateTime)

            if (pickUpDateTime.isBlank() || dropOffDateTime.isBlank()) {
                _imageUiState.value = BookingDetailImageUiState.Empty
                return@launch
            }

            val result = vehicleRepository.searchPrices(
                token = token,
                pickUpDateTime = pickUpDateTime,
                dropOffDateTime = dropOffDateTime,
                pickUpLocationPointId = pickupLocationId.toString(),
                dropOffLocationPointId = dropOffLocationId.toString()
            )

            result.onSuccess { vehicles ->
                val matchedVehicle = vehicles.firstOrNull { it.modelId == vehicleModelId }
                val imageUrl = buildImageUrl(matchedVehicle?.imageList?.firstOrNull().orEmpty())

                if (imageUrl.isNotBlank()) {
                    _imageUiState.value = BookingDetailImageUiState.Success(imageUrl)
                } else {
                    _imageUiState.value = BookingDetailImageUiState.Empty
                }
            }.onFailure { error ->
                _imageUiState.value = BookingDetailImageUiState.Error(
                    error.message ?: "Araç resmi alınamadı"
                )
            }
        }
    }

    private fun formatSearchReservationDateForApi(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))
            val parsedDate = inputFormat.parse(dateTime)
            if (parsedDate != null) outputFormat.format(parsedDate) else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun buildImageUrl(imagePath: String): String {
        if (imagePath.isBlank()) return ""

        return if (imagePath.startsWith("http")) {
            imagePath
        } else {
            val normalizedPath = if (imagePath.startsWith("/")) imagePath else "/$imagePath"
            "https://www.otorentacar.com$normalizedPath"
        }
    }
}