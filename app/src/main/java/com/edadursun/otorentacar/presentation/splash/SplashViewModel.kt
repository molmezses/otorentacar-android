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
import com.edadursun.otorentacar.data.repository.LocationRepository
import com.edadursun.otorentacar.data.remote.model.LocationItemResponse

// Splash ekranının UI durumlarını temsil eder
// Fragment bu state'i dinleyerek ne göstereceğine karar verir
sealed class SplashUiState {
    object Idle : SplashUiState()      // Henüz işlem başlamadı
    object Loading : SplashUiState()   // Token alma/auth işlemi sürüyor
    object Success : SplashUiState()   // Session başarıyla başlatıldı
    data class Error(val message: String) : SplashUiState() // Hata oluştu
}

class SplashViewModel : ViewModel() {

    // Auth endpointine giden repository
    private val authRepository = AuthRepository(RetrofitProvider.authApiService)

    // Auth başarılı olursa token'ı TokenStore'a yazan katman
    private val sessionManager = SessionManager(authRepository)

    // Splash ekranının durumunu tutar
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState: StateFlow<SplashUiState> = _uiState

    // Location endpointine giden repository
    private val locationRepository = LocationRepository(RetrofitProvider.locationApiService)

    // Çekilen lokasyon listesini UI tarafına taşımak için tutulur
    private val _locations = MutableStateFlow<List<LocationItemResponse>>(emptyList())
    val locations: StateFlow<List<LocationItemResponse>> = _locations

    // Dışarıdan username/password verilerek session başlatılır
    fun initializeSession(username: String, password: String) {
        viewModelScope.launch {
            // İşlem başladı, state Loading oluyor
            _uiState.value = SplashUiState.Loading

            val result = sessionManager.initializeSession(
                username = username,
                password = password
            )

            // Auth başarılıysa Success state'ine geç
            // Başarısızsa Error state'ine geç
            result.onSuccess {
                _uiState.value = SplashUiState.Success
            }.onFailure { error ->
                _uiState.value =
                    SplashUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    // BuildConfig içinden username/password alarak session başlatan kısa yol
    // Böylece Fragment artık credentials bilmez
    fun initializeSession() {
        initializeSession(
            username = com.edadursun.otorentacar.BuildConfig.RIKA_USERNAME,
            password = com.edadursun.otorentacar.BuildConfig.RIKA_PASSWORD
        )
    }

    // Token alındıktan sonra test amaçlı lokasyonları çeker
    fun fetchLocationsForTest() {
        viewModelScope.launch {

            // TokenStore içindeki mevcut tokenı al
            val token = TokenStore.token

            // Token yoksa hata state'ine geç
            if (token.isNullOrBlank()) {
                _uiState.value = SplashUiState.Error("Token not found")
                return@launch
            }

            // Token ile searchLocations çağrısı yap
            val result = locationRepository.searchLocations(token)

            result.onSuccess { locations ->
                // Gelen lokasyon listesini state içine yaz
                _locations.value = locations

                // Şu an test amaçlı logcat'e de basıyoruz
                android.util.Log.d("LOCATION_TEST", "Locations count: ${locations.size}")
                locations.forEach { location ->
                    android.util.Log.d(
                        "LOCATION_TEST",
                        "Location -> id: ${location.id}, name: ${location.name}"
                    )
                }
            }.onFailure { error ->
                // Location isteği başarısızsa logcat'e hata bas
                android.util.Log.e("LOCATION_TEST", "Error: ${error.message}")
            }
        }
    }

    // Şu an saklanan tokenı görmek için yardımcı fonksiyon
    fun currentToken(): String? {
        return TokenStore.token
    }
}