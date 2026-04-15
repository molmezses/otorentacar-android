package com.edadursun.otorentacar.data.remote.service

import com.edadursun.otorentacar.data.remote.model.ApiResponse
import com.edadursun.otorentacar.data.remote.model.ConnectResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//Api ile konuşmanın arayüzü
//Hangi endpoint çağrılcak, hangi method kullanılacak
//Hangi parametreler gönderilcek , hangi model döncek
//Ne çağrılacağını tarif eder,ne yapılacağını söyler
interface AuthApiService {

    @FormUrlEncoded
    @POST("ws/auth/v1/tr/connect")
    suspend fun connect(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<ConnectResponse>
}