package com.edadursun.otorentacar.core.network

import com.edadursun.otorentacar.data.remote.service.AuthApiService
import com.edadursun.otorentacar.data.remote.service.LocationApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//Retrofit’i kuruyor , base url’i veriyor , OkHttp motorunu bağlıyor , JSON parser’ı bağlıyor , sonra interface’lerden çalışan servis nesneleri üretiyor
//AuthApiService de yazdığım tarifi alıyor ve koda dönüştürüyor
object RetrofitProvider {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(com.edadursun.otorentacar.BuildConfig.RIKA_BASE_URL)
        .client(OkHttpProvider.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)

    val locationApiService: LocationApiService =
        retrofit.create(LocationApiService::class.java)
}