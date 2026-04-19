package com.edadursun.otorentacar.data.remote.response

import java.io.Serializable

// searchReservation endpointinden dönen data kısmını temsil eder
data class SearchReservationResponse(
    val `object`: ReservationDetailResponse
) : Serializable

// Rezervasyonun tüm detay bilgilerini tutar
data class ReservationDetailResponse(
    val id: Int, // Rezervasyon id
    val vehicleModel: ReservationVehicleModelResponse, // Araç modeli bilgileri
    val fullname: String, // Ad soyad
    val email: String, // E-posta
    val phone1: String, // Telefon
    val birthDate: String, // Doğum tarihi
    val reservationCode: String, // Rezervasyon takip kodu
    val pickUpDateTime: String, // Alış tarih-saat
    val dropOffDateTime: String, // Dönüş tarih-saat
    val status: ReservationStatusResponse, // Rezervasyon durumu
    val totalPrice: Double, // Genel toplam fiyat
    val extraList: List<ReservationExtraItemResponse> = emptyList(), // Seçilen ek hizmetler
    val pickUpLocationPoint: ReservationLocationPointResponse, // Alış lokasyonu
    val dropOffLocationPoint: ReservationLocationPointResponse, // Dönüş lokasyonu
    val reservationPaymentMethod: ReservationPaymentMethodResponse, // Ödeme yöntemi
    val reservationSource: String, // Rezervasyon kaynağı
    val flightNo: String? = null // Uçuş kodu (opsiyonel)
) : Serializable

// Araç model detaylarını tutar
data class ReservationVehicleModelResponse(
    val modelId: Int,
    val brand: ReservationBrandResponse,
    val engine: ReservationEngineResponse,
    val transmission: ReservationTransmissionResponse,
    val name: String,
    val year: Int,
    val imageList: List<String>? = emptyList()
) : Serializable

// Araç markası bilgisi
data class ReservationBrandResponse(
    val id: Int,
    val name: String
) : Serializable

// Araç motor tipi bilgisi
data class ReservationEngineResponse(
    val id: Int,
    val name: String
) : Serializable

// Araç vites tipi bilgisi
data class ReservationTransmissionResponse(
    val id: Int,
    val name: String
) : Serializable

// Rezervasyon durum bilgisi
data class ReservationStatusResponse(
    val id: Int,
    val name: String
) : Serializable

// Para birimi bilgisi
data class ReservationCurrencyResponse(
    val id: Int,
    val code: String,
    val name: String
) : Serializable

// Ek hizmet satırını temsil eder
data class ReservationExtraItemResponse(
    val extra: ReservationExtraResponse,
    val count: Int // Seçilen adet
) : Serializable

// Ek hizmet detay bilgisi
data class ReservationExtraResponse(
    val id: Int,
    val currency: ReservationCurrencyResponse,
    val price: Double,
    val name: String,
    val description: String? = null
) : Serializable

// Lokasyon bilgisi
data class ReservationLocationPointResponse(
    val id: Int,
    val name: String
) : Serializable

// Rezervasyon ödeme yöntemi bilgisi
data class ReservationPaymentMethodResponse(
    val id: Int,
    val name: String
) : Serializable