package com.edadursun.otorentacar.data.model


data class Vehicle(
    val id: Int,
    val name: String,
    val type: String,
    val transmission: String,
    val fuel: String,
    val dailyPrice: String,
    val totalPrice: String,
    val passengerCount: String,
    val bagCount: String,
    val tag: String,
    val imageResId: Int,
    val imageUrl: String,
    val orderNo: Int

)