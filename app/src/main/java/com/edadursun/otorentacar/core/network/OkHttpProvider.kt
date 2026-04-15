package com.edadursun.otorentacar.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

//Bağlantıyı alır,isteği gönderir,cevabı alır,header ekler,loglar
//Gerçek isteği internete gönderiyorrrrrr
object OkHttpProvider {

    fun create(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}