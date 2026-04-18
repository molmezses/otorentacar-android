package com.edadursun.otorentacar.data.remote.service

import com.edadursun.otorentacar.data.remote.model.ApiResponse
import com.edadursun.otorentacar.data.remote.response.AddReservationResponse
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//Rezervasyon oluşturma isteğini tarif ediyoruz
//Hangi endpoint kullanıldı hangi parametreler gerekiyor onları anlattık
interface ReservationApiService {

    @FormUrlEncoded
    @POST("ws/rentacar/v1/tr/addReservation")
    suspend fun addReservation(
        @Field("token") token: String,
        @Field("pickUpLocationPointId") pickUpLocationPointId: String,
        @Field("dropOffLocationPointId") dropOffLocationPointId: String,
        @Field("vehicleModelId") vehicleModelId: String,
        @Field("pickUpDateTime") pickUpDateTime: String,
        @Field("dropOffDateTime") dropOffDateTime: String,
        @Field("name") name: String,
        @Field("surname") surname: String,
        @Field("phone1") phone1: String,
        @Field("email") email: String,
        @Field("birthDate") birthDate: String,
        @Field("flightNo") flightNo: String,
        @Field("totalPrice") totalPrice: String,
        @Field("currencyId") currencyId: String,
        @Field("paymentMethodId") paymentMethodId: String,
        @FieldMap(encoded = true) dynamicFields: Map<String, String>
    ): ApiResponse<AddReservationResponse>
}