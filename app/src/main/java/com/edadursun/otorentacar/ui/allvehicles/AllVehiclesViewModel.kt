package com.edadursun.otorentacar.ui.allvehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.data.remote.response.PriceVehicleItemResponse
import com.edadursun.otorentacar.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Araç listesi ekranında UI'ın hangi durumda olduğunu temsil eder
sealed class AllVehiclesUiState {
    object Idle : AllVehiclesUiState()
    object Loading : AllVehiclesUiState()
    data class Success(val vehicles: List<Vehicle>) : AllVehiclesUiState()
    data class Error(val message: String) : AllVehiclesUiState()
}

// API'den gelen araç verisini UI'ın kullanacağı Vehicle modeline çeviren ViewModel
class AllVehiclesViewModel : ViewModel() {

    // Repository üzerinden API çağrısı yapılır
    private val vehicleRepository = VehicleRepository(RetrofitProvider.vehicleApiService)

    // Ekranın durumunu tutar
    private val _uiState = MutableStateFlow<AllVehiclesUiState>(AllVehiclesUiState.Idle)
    val uiState: StateFlow<AllVehiclesUiState> = _uiState

    // Araçları API'den getirir
    fun fetchVehicles(
        pickUpDateTime: String,
        dropOffDateTime: String,
        pickUpLocationPointId: String,
        dropOffLocationPointId: String,
        pickupMillis: Long,
        dropoffMillis: Long
    ) {
        viewModelScope.launch {
            // Yükleniyor durumuna geç
            _uiState.value = AllVehiclesUiState.Loading

            // Önceden alınmış token'ı çek
            val token = TokenStore.token
            if (token.isNullOrBlank()) {
                _uiState.value = AllVehiclesUiState.Error("Token bulunamadı")
                return@launch
            }

            // Repository üzerinden searchPrices isteği at
            val result = vehicleRepository.searchPrices(
                token = token,
                pickUpDateTime = pickUpDateTime,
                dropOffDateTime = dropOffDateTime,
                pickUpLocationPointId = pickUpLocationPointId,
                dropOffLocationPointId = dropOffLocationPointId
            )

            // Başarılı olursa gelen araçları UI modeline çevir
            result.onSuccess { items ->
                _uiState.value = AllVehiclesUiState.Success(
                    items.map { it.toVehicle(pickupMillis, dropoffMillis) }
                )
            }.onFailure { error ->
                // Hata olursa mesajı UI'a gönder
                _uiState.value = AllVehiclesUiState.Error(
                    error.message ?: "Araçlar alınamadı"
                )
            }
        }
    }

    // API'den gelen tek bir araç verisini, adapter'ın kullandığı Vehicle modeline çevirir
    private fun PriceVehicleItemResponse.toVehicle(
        pickupMillis: Long,
        dropoffMillis: Long
    ): Vehicle {
        // Para birimi EUR ise € göster, değilse backend'den gelen kodu kullan
        val currencyText = if (pricing.currency.code == "EUR") "€" else pricing.currency.code

        // Kullanıcının seçtiği alış ve dönüş saatine göre kiralama gün sayısını hesapla
        val rentalDays = calculateRentalDays(pickupMillis, dropoffMillis)

        // API'den gelen günlük fiyat
        val dailyPriceValue = pricing.dailyPrice

        // Toplam fiyat = günlük fiyat x kiralama günü
        val totalPriceValue = dailyPriceValue * rentalDays

        // UI'da gösterilecek metinler
        val dailyPriceText = "$currencyText${formatPrice(dailyPriceValue)}"
        val totalPriceText = "Toplam $currencyText${formatPrice(totalPriceValue)}"

        return Vehicle(
            id = modelId,
            name = brand.name + " " + name,
            type = type.name,
            transmission = transmission.name,
            fuel = engine.name,
            dailyPrice = dailyPriceText,
            totalPrice = totalPriceText,
            passengerCount = maxPassenger.toString(),
            bagCount = maxBigBaggage.toString(),
            tag = vehicleModelClass.name,
            imageResId = R.drawable.ic_directions_car,
            orderNo = orderNo
        )
    }

    // Alış ve dönüş saatine göre kaç günlük kiralama olduğunu hesaplar
    // Saat farkı tam gün değilse yukarı yuvarlanır
    private fun calculateRentalDays(
        pickupMillis: Long,
        dropoffMillis: Long
    ): Int {
        val diffMillis = dropoffMillis - pickupMillis

        // Hatalı ya da aynı tarih durumunda en az 1 gün kabul et
        if (diffMillis <= 0L) return 1

        // Milisaniyeyi saate çevir
        val diffHours = diffMillis / (1000.0 * 60 * 60)

        // 24 saat = 1 gün, küsuratlıysa yukarı yuvarla
        return kotlin.math.ceil(diffHours / 24.0).toInt().coerceAtLeast(1)
    }

    // Fiyatı gereksiz .0 olmadan ekrana uygun string'e çevirir
    private fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", price)
        }
    }
}