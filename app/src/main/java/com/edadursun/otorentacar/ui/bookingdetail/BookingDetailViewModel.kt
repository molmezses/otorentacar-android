package com.edadursun.otorentacar.ui.bookingdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.remote.response.SearchReservationResponse
import com.edadursun.otorentacar.data.repository.BookingDetailRepository
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

// Booking detail ekranındaki rezervasyon detayı yükleme durumlarını temsil eder
sealed class BookingDetailReservationUiState {
    object Idle : BookingDetailReservationUiState()
    object Loading : BookingDetailReservationUiState()
    data class Success(val response: SearchReservationResponse) : BookingDetailReservationUiState()
    data class Error(val message: String) : BookingDetailReservationUiState()
}

// Rezervasyon detay verisini ve araç görselini yöneten ViewModel
class BookingDetailViewModel : ViewModel() {

    // Araç listesinden ilgili aracın resmini bulmak için kullanılan repository
    private val vehicleRepository = VehicleRepository(RetrofitProvider.vehicleApiService)

    // Reservation code ile rezervasyon detayını çekmek için kullanılan repository
    private val bookingDetailRepository =
        BookingDetailRepository(RetrofitProvider.reservationQueryApiService)

    // Araç resmi için UI state
    private val _imageUiState =
        MutableStateFlow<BookingDetailImageUiState>(BookingDetailImageUiState.Idle)
    val imageUiState: StateFlow<BookingDetailImageUiState> = _imageUiState

    // Rezervasyon detayı için UI state
    private val _reservationUiState =
        MutableStateFlow<BookingDetailReservationUiState>(BookingDetailReservationUiState.Idle)
    val reservationUiState: StateFlow<BookingDetailReservationUiState> = _reservationUiState

    // Reservation code kullanarak backend'den rezervasyon detayını çeker
    fun fetchReservationDetail(reservationCode: String) {
        viewModelScope.launch {
            _reservationUiState.value = BookingDetailReservationUiState.Loading

            // Token yoksa API isteği yapılamaz
            val token = TokenStore.token
            if (token.isNullOrBlank()) {
                _reservationUiState.value =
                    BookingDetailReservationUiState.Error("Token bulunamadı")
                return@launch
            }

            // Reservation code boşsa sorgu yapılamaz
            if (reservationCode.isBlank()) {
                _reservationUiState.value =
                    BookingDetailReservationUiState.Error("Rezervasyon kodu boş")
                return@launch
            }

            // Repository üzerinden reservation detail çağrısını yap
            val result = bookingDetailRepository.searchReservation(
                token = token,
                reservationCode = reservationCode
            )

            // Sonucu UI state'e aktar
            result.onSuccess { response ->
                _reservationUiState.value =
                    BookingDetailReservationUiState.Success(response)
            }.onFailure { error ->
                _reservationUiState.value =
                    BookingDetailReservationUiState.Error(
                        error.message ?: "Rezervasyon detayı alınamadı"
                    )
            }
        }
    }

    // Reservation detail ekranında gösterilecek araç resmini backend'den dolaylı olarak bulur
    fun fetchVehicleImage(
        vehicleModelId: Int,
        pickupLocationId: Int,
        dropOffLocationId: Int,
        rawPickupDateTime: String,
        rawDropOffDateTime: String
    ) {
        viewModelScope.launch {
            _imageUiState.value = BookingDetailImageUiState.Loading

            // Token yoksa fiyat/araç sorgusu yapılamaz
            val token = TokenStore.token
            if (token.isNullOrBlank()) {
                _imageUiState.value = BookingDetailImageUiState.Error("Token bulunamadı")
                return@launch
            }

            // API'nin istediği formatta tarihleri dönüştür
            val pickUpDateTime = formatSearchReservationDateForApi(rawPickupDateTime)
            val dropOffDateTime = formatSearchReservationDateForApi(rawDropOffDateTime)

            // Tarihler dönüştürülemediyse resim sorgusu yapma
            if (pickUpDateTime.isBlank() || dropOffDateTime.isBlank()) {
                _imageUiState.value = BookingDetailImageUiState.Empty
                return@launch
            }

            // SearchPrices endpointi ile ilgili tarihlerdeki araç listesini çek
            val result = vehicleRepository.searchPrices(
                token = token,
                pickUpDateTime = pickUpDateTime,
                dropOffDateTime = dropOffDateTime,
                pickUpLocationPointId = pickupLocationId.toString(),
                dropOffLocationPointId = dropOffLocationId.toString()
            )

            result.onSuccess { vehicles ->
                // Gelen araçlar içinde bizim reservation detail'deki modelId ile eşleşeni bul
                val matchedVehicle = vehicles.firstOrNull { it.modelId == vehicleModelId }

                // Eşleşen araçtaki ilk resmi tam url'e çevir
                val imageUrl = buildImageUrl(matchedVehicle?.imageList?.firstOrNull().orEmpty())

                // Resim url'i varsa success, yoksa empty döndür
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

    // SearchPrices endpointinin istediği tarih formatına çevirir
    // Girdi: yyyy-MM-dd HH:mm:ss
    // Çıktı: dd.MM.yyyy HH:mm
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

    // API'den gelen göreceli resim yolunu tam url haline getirir
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