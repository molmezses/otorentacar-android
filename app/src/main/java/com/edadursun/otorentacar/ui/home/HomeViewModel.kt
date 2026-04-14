package com.edadursun.otorentacar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.remote.model.LocationItemResponse
import com.edadursun.otorentacar.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Home ekranının genel durumunu tutar
sealed class HomeUiState {
    object Idle : HomeUiState()                  // Henüz işlem başlamadı
    object Loading : HomeUiState()               // Lokasyonlar yükleniyor
    object Success : HomeUiState()               // Lokasyonlar başarıyla alındı
    data class Error(val message: String) : HomeUiState() // Hata oluştu
}

class HomeViewModel : ViewModel() {

    // Lokasyon endpointine giden repository
    private val locationRepository = LocationRepository(RetrofitProvider.locationApiService)

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

    // Kullanıcının seçtiği pickup lokasyonunu günceller
    fun selectPickupLocation(location: LocationItemResponse) {
        _selectedPickupLocation.value = location
    }

    // Kullanıcının seçtiği dropoff lokasyonunu günceller
    fun selectDropOffLocation(location: LocationItemResponse) {
        _selectedDropOffLocation.value = location
    }
}