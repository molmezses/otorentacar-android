package com.edadursun.otorentacar.data.remote.model

//Ortak API response modeli - DIŞ RESPONSE
//Amaç: her endpoint için tekrar tekrar status,data,description yazmamak
data class ApiResponse<T>(
    val status: Int, //1 başarılı 1 dışında her şey başarısız
    val data: T?,    //sorguya dönen json tipindeki nesne
    val description: String? //hata açıklaması
)
