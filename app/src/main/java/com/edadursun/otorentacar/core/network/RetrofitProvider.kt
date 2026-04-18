package com.edadursun.otorentacar.core.network

import com.edadursun.otorentacar.data.remote.service.AuthApiService
import com.edadursun.otorentacar.data.remote.service.ExtrasApiService
import com.edadursun.otorentacar.data.remote.service.LocationApiService
import com.edadursun.otorentacar.data.remote.service.ReservationApiService
import com.edadursun.otorentacar.data.remote.service.ReservationQueryApiService
import com.edadursun.otorentacar.data.remote.service.VehicleApiService
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

    //LocationApiService i çalışan nesneye çevirir
    val locationApiService: LocationApiService = retrofit.create(LocationApiService::class.java)

    //VehicleApiService i çalışan nesneye çevirir
    val vehicleApiService: VehicleApiService = retrofit.create(VehicleApiService::class.java)

    //ExtrasApiService i çalışan nesneye çevirir
    val extrasApiService : ExtrasApiService = retrofit.create(ExtrasApiService::class.java)

    //ReservationApiService i çalışan nsneye dönüştürür
    val reservationApiService: ReservationApiService by lazy {
        retrofit.create(ReservationApiService::class.java)
    }

    //ReservationQueryServiceApi i çalışan nesneye dönüştürür
    val reservationQueryApiService : ReservationQueryApiService by lazy {
        retrofit.create(ReservationQueryApiService::class.java)
    }
}