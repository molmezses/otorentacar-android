package com.edadursun.otorentacar.data.remote.request

//searchPrices isteği API'ye hangi bilgilerle gidecek onu tutuyoruz
data class SearchPricesRequest(
    val token: String,
    val pickUpDateTime: String,
    val dropOffDateTime: String,
    val pickUpLocationPointId: String,
    val dropOffLocationPointId: String
)