package com.edadursun.otorentacar.data.remote.response

//searchPrices endpointine istek attığımızda dönen yanıttaki data kısmını temsil eder
data class SearchPricesResponse(
    val list:List<PriceVehicleItemResponse>
)
