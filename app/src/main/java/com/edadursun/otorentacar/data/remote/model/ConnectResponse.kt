package com.edadursun.otorentacar.data.remote.model

//connect başarılı olunca data içinde şunlar geliyor
//ApiResponse<T> sadece dış yapıyı temsil ediyor.Ama data kısmının içeriği endpoint’e göre değişiyor:
//Bu yüzden her endpoint ayrı data modeli gerekir - İÇ DATA
data class ConnectResponse(
    val token:String,
    val lifeTime : String
)
