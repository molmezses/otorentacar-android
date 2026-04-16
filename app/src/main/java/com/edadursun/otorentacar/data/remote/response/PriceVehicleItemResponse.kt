package com.edadursun.otorentacar.data.remote.response

//searchPrices endpointine attığımız istek sonucunda verdiği yanıtta tek bir araç öğesini temsil eder
//Listedeki tek araç
data class PriceVehicleItemResponse(
    val modelId: Int,
    val brand: BrandResponse,
    val type: TypeResponse,
    val engine: EngineResponse,
    val transmission: TransmissionResponse,
    val name: String,
    val maxPassenger: Int,
    val maxSmallBaggage: Int,
    val maxBigBaggage: Int,
    val doorCount: Int,
    val hasGps: Boolean,
    val hasFrontSensor: Boolean,
    val hasBackSensor: Boolean,
    val hasAirConditioner: Boolean,
    val hasRadio: Boolean,
    val hasCdPlayer: Boolean,
    val imageList: List<String>,
    val year: Int,
    val pricing: PricingResponse,
    val vehicleModelClass: VehicleModelClassResponse,
    val orderNo: Int
)

data class BrandResponse(
    val id: Int,
    val name: String
)

data class TypeResponse(
    val id: Int,
    val name: String,
    val activeInTransfer: Boolean,
    val imageList: List<String>
)

data class EngineResponse(
    val id: Int,
    val name: String
)

data class TransmissionResponse(
    val id: Int,
    val name: String
)

data class PricingResponse(
    val season: SeasonResponse,
    val dailyPrice: Double,
    val currency: CurrencyResponse
)

data class SeasonResponse(
    val startDate: String,
    val endDate: String
)

data class CurrencyResponse(
    val id: Int,
    val code: String
)

data class VehicleModelClassResponse(
    val id: Int,
    val name: String
)