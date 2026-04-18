package com.edadursun.otorentacar.data.remote.request

// Rezervasyon oluşturma endpointine gönderilecek tüm verileri tutar
data class AddReservationRequest(
    val token: String,
    val pickUpLocationPointId: String,
    val dropOffLocationPointId: String,
    val vehicleModelId: String,
    val pickUpDateTime: String,
    val dropOffDateTime: String,
    val name: String,
    val surname: String,
    val phone1: String,
    val email: String,
    val birthDate: String,
    val flightNo: String,
    val totalPrice: String,
    val currencyId: String,
    val paymentMethodId: String,
    val dynamicFields: Map<String, String>
)