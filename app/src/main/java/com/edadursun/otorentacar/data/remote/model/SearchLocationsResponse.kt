package com.edadursun.otorentacar.data.remote.model

//Endpointten dönen cevabın data kısmını temsil ediyor
//datanın iiçnde list var bu listi bu karşılıyor
data class SearchLocationsResponse(
    val list: List<LocationItemResponse>
)
