package com.edadursun.otorentacar.data.repository

import com.edadursun.otorentacar.data.remote.response.PriceVehicleItemResponse
import com.edadursun.otorentacar.data.remote.service.VehicleApiService

//VehicleApiService ile gerçek isteği atar
//Responsun başarılı olup olmadığını kontrol eder başarılıysa araç listesini döndürür
//Araç arama servisinden gelen ham API sonucunu, ViewModel’in kullanacağı temiz listeye dönüştüren katman
class VehicleRepository(
    private val vehicleApiService: VehicleApiService
) {

    suspend fun searchPrices(
        token: String,
        pickUpDateTime: String,
        dropOffDateTime: String,
        pickUpLocationPointId: String,
        dropOffLocationPointId:String
    ): Result<List<PriceVehicleItemResponse>> {
        return try {
            val response = vehicleApiService.searchPrices(
                token = token,
                pickUpDateTime = pickUpDateTime,
                dropOffDateTime = dropOffDateTime,
                pickUpLocationPointId = pickUpLocationPointId,
                dropOffLocationPointId = dropOffLocationPointId
            )

            val list = response.data?.list
                    if (response.status == 1 && list != null) {
                        Result.success(list)
                    } else {
                        Result.failure(
                            Exception(response.description ?: "Search prices failed")
                        )
                    }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}