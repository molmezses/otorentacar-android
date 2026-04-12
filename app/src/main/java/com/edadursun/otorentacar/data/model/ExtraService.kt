package com.edadursun.otorentacar.data.model

data class ExtraService(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    var quantity: Int = 1
)