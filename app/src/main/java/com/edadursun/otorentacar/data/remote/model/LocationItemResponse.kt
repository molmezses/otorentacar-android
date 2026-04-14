package com.edadursun.otorentacar.data.remote.model

//APIden gelen lokasyon nesnesi
//Tek bir lokasyon satırı. her bir lokasyonun idsi ve namei var
//listin içindeki tek tek ögeler
data class LocationItemResponse(
    val id: Int,
    val name: String
)