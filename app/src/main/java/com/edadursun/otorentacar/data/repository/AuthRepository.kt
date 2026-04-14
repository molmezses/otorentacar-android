package com.edadursun.otorentacar.data.repository

import com.edadursun.otorentacar.data.remote.model.ConnectResponse
import com.edadursun.otorentacar.data.remote.service.AuthApiService

//Viewmodel ile remote katman arasındaki köprü
//Servicei çağırır,username/password verir,sonucu düzenler,hata olursa yakalar,viewmodele temiz sonuç döner
//Veriyi getirir, tokeni getirir, isteği atar sonucu düzenler
class AuthRepository(
    private val authApiService: AuthApiService
) {

    suspend fun connect(
        username: String,
        password: String
    ): Result<ConnectResponse> {
        return try {
            val response = authApiService.connect(
                username = username,
                password = password
            ) //gerçek endpoint çağrısını yapar

            if (response.status == 1 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(
                    Exception(response.description ?: "Connect request failed")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}