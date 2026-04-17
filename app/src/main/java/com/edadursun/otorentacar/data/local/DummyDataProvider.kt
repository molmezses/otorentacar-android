package com.edadursun.otorentacar.data.local

import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.model.Vehicle
import com.edadursun.otorentacar.data.model.VehicleCategory

object DummyDataProvider {

    fun getFeaturedVehicles(): List<Vehicle> {
        return listOf(
            Vehicle(
                id = 1,
                name = "Renault Clio",
                type = "ECONOMY HATCHBACK",
                transmission = "Otomatik",
                fuel = "Benzin",
                dailyPrice = "₺1.250",
                totalPrice = "₺3.750",
                passengerCount = "5",
                bagCount = "2",
                tag = "POPULAR",
                imageResId = R.drawable.car_placeholder,
                orderNo = 3
            ),
            Vehicle(
                id = 2,
                name = "Fiat Egea",
                type = "COMFORT SEDAN",
                transmission = "Otomatik",
                fuel = "Benzin",
                dailyPrice = "₺1.450",
                totalPrice = "₺4.350",
                passengerCount = "5",
                bagCount = "2",
                tag = "ÖNERİLEN",
                imageResId = R.drawable.car_placeholder,
                orderNo = 3
            ),
            Vehicle(
                id = 3,
                name = "Toyota Corolla",
                type = "ECO HYBRID",
                transmission = "Otomatik",
                fuel = "Benzin",
                dailyPrice = "₺1.850",
                totalPrice = "₺5.550",
                passengerCount = "5",
                bagCount = "2",
                tag = "POPULAR",
                imageResId = R.drawable.car_placeholder,
                orderNo = 3
            )
        )
    }

    fun getVehicleCategories(): List<VehicleCategory> {
        return listOf(
            VehicleCategory(
                id = 1,
                name = "Ekonomik",
                vehicleCount = "42 ARAÇ",
                iconResId = android.R.drawable.ic_menu_compass
            ),
            VehicleCategory(
                id = 2,
                name = "Lüks",
                vehicleCount = "12 ARAÇ",
                iconResId = android.R.drawable.btn_star_big_on
            ),
            VehicleCategory(
                id = 3,
                name = "SUV",
                vehicleCount = "18 ARAÇ",
                iconResId = android.R.drawable.ic_menu_directions
            )
        )
    }
}