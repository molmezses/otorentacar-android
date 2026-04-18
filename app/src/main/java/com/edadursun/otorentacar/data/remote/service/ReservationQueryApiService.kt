package com.edadursun.otorentacar.data.remote.service

import com.edadursun.otorentacar.data.remote.model.ApiResponse
import com.edadursun.otorentacar.data.remote.response.SearchReservationResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//Hangi endpointe istek atılcak hangi metotlar kulanılcak hangi parametreler önderilcek onu tarif ediyoruz
interface ReservationQueryApiService {

    @FormUrlEncoded
    @POST("ws/rentacar/v1/tr/searchReservation")
    suspend fun searchReservation(
        @Field("token") token: String,
        @Field("reservationCode") reservationCode: String
    ): ApiResponse<SearchReservationResponse>
}