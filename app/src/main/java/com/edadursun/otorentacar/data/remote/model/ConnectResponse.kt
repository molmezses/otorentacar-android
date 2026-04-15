package com.edadursun.otorentacar.data.remote.model

//Endpoint başarılı olursa data kısmında bu veriler geliyor. Endpoint özeldir . Connect endpointinin iç verisi ne
//Bu kısmı da modele çevirmek için oluşturuldu
data class ConnectResponse(
    val token:String,
    val lifeTime : String
)
