package com.edadursun.otorentacar.data.repository

import com.edadursun.otorentacar.data.remote.response.SearchReservationResponse
import com.edadursun.otorentacar.data.remote.service.ReservationQueryApiService

// Rezervasyon sorgulama isteğini API'ye gönderir
// Gelen sonucu Result yapısına çevirerek ViewModel'e iletir
class ReservationQueryRepository(
    private val reservationQueryApiService: ReservationQueryApiService
) {

    suspend fun searchReservation(
        token: String,
        reservationCode: String
    ): Result<SearchReservationResponse> {
        return try {
            // API isteğini gönder
            val response = reservationQueryApiService.searchReservation(
                token = token,
                reservationCode = reservationCode
            )

            // Başarılı response geldiyse data kısmını döndür
            if (response.status == 1 && response.data != null) {
                Result.success(response.data)
            } else {
                // API hata döndürdüyse description mesajını kullan
                Result.failure(
                    Exception(response.description ?: "Rezervasyon bulunamadı")
                )
            }
        } catch (e: Exception) {
            // Ağ hatası vb. durumları yakala
            Result.failure(e)
        }
    }
}