package com.edadursun.otorentacar.data.repository

import com.edadursun.otorentacar.data.remote.model.LocationItemResponse
import com.edadursun.otorentacar.data.remote.service.LocationApiService

//İstek sonucu API den gelen cevabı uygulama için kullanılır hale getiriyor
class LocationRepository(
    private val locationApiService: LocationApiService
) {
    
    suspend fun searchLocations(token: String): Result<List<LocationItemResponse>> {
        return try {
            val response = locationApiService.searchLocations(token)

            if (response.status == 1 && response.data != null) {
                Result.success(response.data.list)
            } else {
                Result.failure(
                    Exception(response.description ?: "Search locations failed")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}