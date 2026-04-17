package com.edadursun.otorentacar.data.remote.response

//searchExtraServices endpointine istek attığımızda dönen yanıttaki data kısmını temsil eder
data class SearchExtraServicesResponse(
    val list:List<ExtraServiceItemResponse>
)
