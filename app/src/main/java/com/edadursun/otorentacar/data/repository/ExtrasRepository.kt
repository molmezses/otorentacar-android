package com.edadursun.otorentacar.data.repository

import com.edadursun.otorentacar.data.remote.response.ExtraServiceItemResponse
import com.edadursun.otorentacar.data.remote.service.ExtrasApiService

//ExtrasApiService üzerinden isteği atıyor
//response başarılıysa response.data.list döndürüyor değilse error döndürüyor
class ExtrasRepository(
    private val extrasApiService: ExtrasApiService
) {

    suspend fun searchExtraServices(token: String): Result<List<ExtraServiceItemResponse>> {
        return try {
            val response = extrasApiService.searchExtras(token)

            if (response.status == 1 && response.data != null) {
                Result.success(response.data.list)
            } else {
                Result.failure(
                    Exception(response.description ?: "Extra services failed")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}