package com.edadursun.otorentacar.data.model

// Rezervasyonlarım ekranında kart içinde göstereceğimiz verileri tutar
data class ReservationUiModel(
    val reservationCode: String,
    val vehicleName: String,
    val statusText: String,
    val pickupLocation: String,
    val dropOffLocation: String,
    val dateRangeText: String,
    val totalPriceText: String
)