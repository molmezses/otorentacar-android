package com.edadursun.otorentacar.data.repository

import com.edadursun.otorentacar.data.remote.response.SearchReservationResponse
import com.edadursun.otorentacar.data.remote.service.ReservationQueryApiService

class BookingDetailRepository(
    private val reservationQueryApiService: ReservationQueryApiService
) {

    suspend fun searchReservation(
        token: String,
        reservationCode: String
    ): Result<SearchReservationResponse> {
        return try {
            val response = reservationQueryApiService.searchReservation(
                token = token,
                reservationCode = reservationCode
            )

            if (response.status == 1 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(
                    Exception(response.description ?: "Rezervasyon bulunamadı")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}