package com.edadursun.otorentacar.ui.extras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edadursun.otorentacar.core.network.RetrofitProvider
import com.edadursun.otorentacar.core.session.TokenStore
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.data.remote.response.ExtraServiceItemResponse
import com.edadursun.otorentacar.data.repository.ExtrasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Extras ekranının UI durumlarını temsil eder
sealed class ExtrasUiState {
    object Idle : ExtrasUiState()
    object Loading : ExtrasUiState()
    data class Success(val services: List<ExtraService>) : ExtrasUiState()
    data class Error(val message: String) : ExtrasUiState()
}

// Ek hizmetler ekranının iş mantığını yöneten ViewModel
class ExtrasViewModel : ViewModel() {

    // Repository üzerinden API çağrıları yapılır
    private val repository = ExtrasRepository(RetrofitProvider.extrasApiService)

    // Genel ekran durumu
    private val _uiState = MutableStateFlow<ExtrasUiState>(ExtrasUiState.Idle)
    val uiState: StateFlow<ExtrasUiState> = _uiState

    // Ekranda gösterilecek ek hizmet listesi
    private val _services = MutableStateFlow<List<ExtraService>>(emptyList())
    val services: StateFlow<List<ExtraService>> = _services

    // API'den ek hizmetleri getirir
    fun fetchExtraServices() {
        viewModelScope.launch {
            _uiState.value = ExtrasUiState.Loading

            // Token kontrolü
            val token = TokenStore.token
            if (token.isNullOrBlank()) {
                _uiState.value = ExtrasUiState.Error("Token bulunamadı")
                return@launch
            }

            // Repository üzerinden endpoint çağrılır
            val result = repository.searchExtraServices(token)

            result.onSuccess { items ->
                // Sadece aktif olan hizmetleri al
                // Sıralama için orderNo'ya göre sırala
                // Sonra UI modeline dönüştür
                val mapped = items
                    .filter { it.isActive == 1 }
                    .sortedBy { it.orderNo }
                    .map { it.toExtraService() }

                _services.value = mapped
                _uiState.value = ExtrasUiState.Success(mapped)
            }.onFailure { error ->
                _uiState.value = ExtrasUiState.Error(
                    error.message ?: "Ek hizmetler alınamadı"
                )
            }
        }
    }

    // Tek seçimlik hizmetlerde seç / kaldır işlemi
    fun toggleSingleSelection(serviceId: Int) {
        _services.value = _services.value.map { service ->
            if (service.id == serviceId) {
                if (service.maxCount == 1) {
                    service.copy(
                        isSelected = !service.isSelected,
                        quantity = if (!service.isSelected) 1 else 0
                    )
                } else {
                    service
                }
            } else {
                service
            }
        }
    }

    // Adetli hizmetlerde sayıyı artırır
    // maxCount sınırını geçmez
    fun increaseQuantity(serviceId: Int) {
        _services.value = _services.value.map { service ->
            if (service.id == serviceId && service.maxCount > 1) {
                val newQuantity = (service.quantity + 1).coerceAtMost(service.maxCount)

                val newChildAges = if (service.name.contains("Bebek Koltuğu", ignoreCase = true)) {
                    service.childAges + ""
                } else {
                    service.childAges
                }.take(newQuantity)

                service.copy(
                    quantity = newQuantity,
                    isSelected = newQuantity > 0,
                    childAges = newChildAges
                )
            } else {
                service
            }
        }
    }

    // Adetli hizmetlerde sayıyı azaltır
    // 0'ın altına düşmez
    fun decreaseQuantity(serviceId: Int) {
        _services.value = _services.value.map { service ->
            if (service.id == serviceId && service.maxCount > 1) {
                val newQuantity = (service.quantity - 1).coerceAtLeast(0)

                val newChildAges = if (service.name.contains("Bebek Koltuğu", ignoreCase = true)) {
                    service.childAges.take(newQuantity)
                } else {
                    service.childAges
                }

                service.copy(
                    quantity = newQuantity,
                    isSelected = newQuantity > 0,
                    childAges = newChildAges
                )
            } else {
                service
            }
        }
    }

    fun updateChildAge(serviceId: Int, index: Int, value: String) {
        _services.value = _services.value.map { service ->
            if (service.id == serviceId) {
                val updatedAges = service.childAges.toMutableList()
                if (index in updatedAges.indices) {
                    updatedAges[index] = value
                }
                service.copy(childAges = updatedAges)
            } else {
                service
            }
        }
    }

    // API'den gelen ham extra service response'unu
    // UI'da kullanılacak ExtraService modeline çevirir
    private fun ExtraServiceItemResponse.toExtraService(): ExtraService {
        return ExtraService(
            id = id,
            name = name,
            description = description ?: "",
            price = price,
            currencySymbol = currency.symbol,
            maxCount = maxCount,
            priceCalculationType = priceCalculationType.name,
            orderNo = orderNo
        )
    }
}