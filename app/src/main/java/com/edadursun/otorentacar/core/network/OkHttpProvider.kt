package com.edadursun.otorentacar.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

//Gerçek network işini yapar
//Bağlantıyı açar,isteği gönderir,cevabı alır,header ekler,loglar
//Gerçek isteği internete gönderiyor
object OkHttpProvider {

    fun create(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}