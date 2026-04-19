package com.edadursun.otorentacar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.data.remote.model.LocationItemResponse
import com.edadursun.otorentacar.data.remote.response.PriceVehicleItemResponse
import com.edadursun.otorentacar.data.repository.LocationRepository
import com.edadursun.otorentacar.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Home ekranının genel durumunu tutar
sealed class HomeUiState {
    object Idle : HomeUiState()                  // Henüz işlem başlamadı
    object Loading : HomeUiState()               // Lokasyonlar veya araçlar yükleniyor
    object Success : HomeUiState()               // Veriler başarıyla alındı
    data class Error(val message: String) : HomeUiState() // Hata oluştu
}

class HomeViewModel : ViewModel() {

    // Lokasyon endpointine giden repository
    private val locationRepository = LocationRepository(RetrofitProvider.locationApiService)

    // Araç fiyat / araç liste endpointine giden repository
    private val vehicleRepository = VehicleRepository(RetrofitProvider.vehicleApiService)

    // Türkiye saat dilimi
    private val turkeyTimeZone: TimeZone = TimeZone.getTimeZone("Europe/Istanbul")

    // Ekranın loading/success/error durumunu tutar
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState

    // API'den gelen tüm lokasyon listesi
    private val _locations = MutableStateFlow<List<LocationItemResponse>>(emptyList())
    val locations: StateFlow<List<LocationItemResponse>> = _locations

    // Kullanıcının seçtiği alış noktası
    private val _selectedPickupLocation = MutableStateFlow<LocationItemResponse?>(null)
    val selectedPickupLocation: StateFlow<LocationItemResponse?> = _selectedPickupLocation

    // Kullanıcının seçtiği dönüş/bırakış noktası
    private val _selectedDropOffLocation = MutableStateFlow<LocationItemResponse?>(null)
    val selectedDropOffLocation: StateFlow<LocationItemResponse?> = _selectedDropOffLocation

    // Home ekranında gösterilecek öne çıkan araçlar
    private val _featuredVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val featuredVehicles: StateFlow<List<Vehicle>> = _featuredVehicles

    // Token ile API'den lokasyonları çeker
    fun fetchLocations() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            // Token yoksa istek atamayız
            val token = TokenStore.token
            if (token.isNullOrBlank()) {
                _uiState.value = HomeUiState.Error("Token bulunamadı")
                return@launch
            }

            // Repository üzerinden lokasyonları al
            val result = locationRepository.searchLocations(token)

            result.onSuccess { locations ->
                _locations.value = locations
                _uiState.value = HomeUiState.Success
            }.onFailure { error ->
                _uiState.value = HomeUiState.Error(
                    error.message ?: "Lokasyonlar alınamadı"
                )
            }
        }
    }

    // API'den gelen araç listesinin ilk 3 tanesini home ekranında gösterir
    fun fetchFeaturedVehicles(
        pickupMillis: Long,
        dropoffMillis: Long,
        pickupLocationId: Int,
        dropOffLocationId: Int
    ) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            // Token yoksa istek atamayız
            val token = TokenStore.token
            if (token.isNullOrBlank()) {
                _uiState.value = HomeUiState.Error("Token bulunamadı")
                return@launch
            }

            // API'nin istediği tarih formatına çevir
            val pickUpDateTime = formatApiDateTime(pickupMillis)
            val dropOffDateTime = formatApiDateTime(dropoffMillis)

            // Repository üzerinden araçları al
            val result = vehicleRepository.searchPrices(
                token = token,
                pickUpDateTime = pickUpDateTime,
                dropOffDateTime = dropOffDateTime,
                pickUpLocationPointId = pickupLocationId.toString(),
                dropOffLocationPointId = dropOffLocationId.toString()
            )

            result.onSuccess { priceItems ->
                // Gelen araç listesini ekranda kullanılacak Vehicle modeline dönüştürüp
                // ilk 3 tanesini öne çıkan araçlar olarak göster
                _featuredVehicles.value = priceItems
                    .sortedBy { it.orderNo }
                    .map { it.toVehicle() }
                    .take(3)

                _uiState.value = HomeUiState.Success
            }.onFailure { error ->
                _featuredVehicles.value = emptyList()
                _uiState.value = HomeUiState.Error(
                    error.message ?: "Öne çıkan araçlar alınamadı"
                )
            }
        }
    }

    // Kullanıcının seçtiği pickup lokasyonunu günceller
    fun selectPickupLocation(location: LocationItemResponse) {
        _selectedPickupLocation.value = location
    }

    // Kullanıcının seçtiği dropoff lokasyonunu günceller
    fun selectDropOffLocation(location: LocationItemResponse) {
        _selectedDropOffLocation.value = location
    }

    // Milisaniye tarihini API'nin istediği formata çevirir
    private fun formatApiDateTime(millis: Long): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr")).apply {
            timeZone = turkeyTimeZone
        }.format(millis)
    }

    // API'den gelen araç fiyat sonucunu uygulamadaki Vehicle modeline dönüştürür
    private fun PriceVehicleItemResponse.toVehicle(): Vehicle {
        return Vehicle(
            id = modelId,
            name = "${brand.name} $name",
            type = type.name,
            transmission = transmission.name,
            fuel = engine.name,
            dailyPrice = formatPrice(pricing.dailyPrice),
            totalPrice = formatPrice(pricing.dailyPrice),
            passengerCount = maxPassenger.toString(),
            bagCount = (maxSmallBaggage + maxBigBaggage).toString(),
            tag = vehicleModelClass.name,
            imageResId = R.drawable.ic_directions_car,
            imageUrl = buildImageUrl(imageList.firstOrNull().orEmpty()),
            orderNo = orderNo
        )
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

    // Fiyatı string formata çevirir
    private fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            "€${price.toInt()}"
        } else {
            "€${String.format(Locale.US, "%.2f", price)}"
        }
    }
}