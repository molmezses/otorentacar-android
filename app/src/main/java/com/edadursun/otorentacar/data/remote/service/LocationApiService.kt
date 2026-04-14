package com.edadursun.otorentacar.data.remote.service

import com.edadursun.otorentacar.data.remote.model.ApiResponse
import com.edadursun.otorentacar.data.remote.model.SearchLocationsResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//Lokasyon endpointini tarif eder,isteği nasıl atacağını tarif ediyoruz
interface LocationApiService {

    @FormUrlEncoded
    @POST("ws/rentacar/v1/tr/searchLocations")
    suspend fun searchLocations(
        @Field("token") token: String
    ): ApiResponse<SearchLocationsResponse>
}