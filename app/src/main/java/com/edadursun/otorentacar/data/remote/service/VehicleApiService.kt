package com.edadursun.otorentacar.data.remote.service

import com.edadursun.otorentacar.data.remote.model.ApiResponse
import com.edadursun.otorentacar.data.remote.response.SearchPricesResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//Hangi endpoint çağrılcak tarif ettik
//Hangi parametreler gönderilcek hangi metot kullanılcak falan
interface VehicleApiService {

    @FormUrlEncoded
    @POST("ws/rentacar/v1/tr/searchPrices")
    suspend fun searchPrices(
        @Field("token") token: String,
        @Field("pickUpDateTime") pickUpDateTime: String,
        @Field("dropOffDateTime") dropOffDateTime: String,
        @Field("pickUpLocationPointId") pickUpLocationPointId: String,
        @Field("dropOffLocationPointId") dropOffLocationPointId: String
    ): ApiResponse<SearchPricesResponse>

}