package com.edadursun.otorentacar.data.remote.model

//APIdan gelen cevabın dış kabuğu - Kapalı bi zarf içerisinde 3 parça içeriği var
data class ApiResponse<T>(
    val status: Int, //1 başarılı 1 dışında her şey başarısız
    val data: T?,    //sorguya dönen json tipindeki nesne
    val description: String? //hata açıklaması
)
