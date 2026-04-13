package com.edadursun.otorentacar.core.network

import com.edadursun.otorentacar.data.remote.service.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//AuthApiService interfaceindeki tariften gerçek API servisi üreten araç
//Interfacei okuyup çalıştırıyor , çalışan api servisine dönüştürüyor
object RetrofitProvider {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.otorentacar.com/")
        .client(OkHttpProvider.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}