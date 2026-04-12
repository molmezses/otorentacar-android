package com.edadursun.otorentacar.data.model

data class InsurancePackage(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val dayText: String = "3 gün",
    var isSelected: Boolean = false
)