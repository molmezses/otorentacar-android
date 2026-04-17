package com.edadursun.otorentacar.data.remote.service

import com.edadursun.otorentacar.data.remote.model.ApiResponse
import com.edadursun.otorentacar.data.remote.response.SearchExtraServicesResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//ExtraService endpointini tarif eder,isteği nasıl atacağını tarif ediyoruz
interface ExtrasApiService {

    @FormUrlEncoded
    @POST("ws/rentacar/v1/tr/searchExtraServices")
    suspend fun searchExtras(
        @Field("token") token: String
    ): ApiResponse<SearchExtraServicesResponse>

}