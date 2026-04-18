package com.edadursun.otorentacar.data.repository

import com.edadursun.otorentacar.data.remote.request.AddReservationRequest
import com.edadursun.otorentacar.data.remote.response.AddReservationResponse
import com.edadursun.otorentacar.data.remote.service.ReservationApiService

class ReservationRepository(
    private val reservationApiService: ReservationApiService
) {

    suspend fun addReservation(
        request: AddReservationRequest
    ): Result<AddReservationResponse> {
        return try {
            val response = reservationApiService.addReservation(
                token = request.token,
                pickUpLocationPointId = request.pickUpLocationPointId,
                dropOffLocationPointId = request.dropOffLocationPointId,
                vehicleModelId = request.vehicleModelId,
                pickUpDateTime = request.pickUpDateTime,
                dropOffDateTime = request.dropOffDateTime,
                name = request.name,
                surname = request.surname,
                phone1 = request.phone1,
                email = request.email,
                birthDate = request.birthDate,
                flightNo = request.flightNo,
                totalPrice = request.totalPrice,
                currencyId = request.currencyId,
                paymentMethodId = request.paymentMethodId,
                dynamicFields = request.dynamicFields
            )

            if (response.status == 1 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(
                    Exception(response.description ?: "Rezervasyon oluşturulamadı")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}