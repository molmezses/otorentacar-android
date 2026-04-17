package com.edadursun.otorentacar.data.remote.response

//searchExtraServices endpointine attığımız istek sonucunda verdiği yanıtta tek bir extra öğesini temsil eder
//Listedeki tek bir extra service
data class ExtraServiceItemResponse(
    val id : Int,
    val currency : ExtraServicesCurrencyResponse,
    val price : Double,
    val orderNo:Int,
    val isActive : Int,
    val name : String,
    val imgPath:String,
    val maxCount:Int,
    val iconCss : String,
    val description:String?,
    val priceCalculationType : PriceCalculationTypeResponse
)

data class ExtraServicesCurrencyResponse(
    val id : Int,
    val active : Int ,
    val symbolDirection:String ,
    val symbol : String,
    val code : String,
    val name : String
)

data class PriceCalculationTypeResponse(
    val id : Int,
    val name : String
)