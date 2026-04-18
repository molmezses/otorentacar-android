package com.edadursun.otorentacar.data.model


data class ExtraService(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val currencySymbol: String,
    val maxCount: Int,
    val priceCalculationType: String,
    val orderNo: Int,
    val isSelected: Boolean = false,
    val quantity: Int = 0,
    val childAges: List<String> = emptyList()
)