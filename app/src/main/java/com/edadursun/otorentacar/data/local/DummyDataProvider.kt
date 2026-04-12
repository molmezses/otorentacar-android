package com.edadursun.otorentacar.data.local

import com.edadursun.otorentacar.R
import com.edadursun.otorentacar.data.model.ExtraService
import com.edadursun.otorentacar.data.model.InsurancePackage
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
                imageResId = R.drawable.car_placeholder
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
                imageResId = R.drawable.car_placeholder
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
                imageResId = R.drawable.car_placeholder
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

    fun getVehicleList(): List<Vehicle> {
        return listOf(
            Vehicle(
                id = 1,
                name = "Renault Clio",
                type = "Economy Hatchback",
                transmission = "Manuel",
                fuel = "Dizel",
                dailyPrice = "₺1.250",
                totalPrice = "₺3.750",
                passengerCount = "5",
                bagCount = "2",
                tag = "POPULAR",
                imageResId = R.drawable.car_placeholder
            ),
            Vehicle(
                id = 2,
                name = "Fiat Egea",
                type = "Sedan",
                transmission = "Auto",
                fuel = "Benzin",
                dailyPrice = "₺1.450",
                totalPrice = "₺4.350",
                passengerCount = "5",
                bagCount = "2",
                tag = "ÖNERİLEN",
                imageResId = R.drawable.car_placeholder
            ),
            Vehicle(
                id = 3,
                name = "Volkswagen Tiguan",
                type = "Premium SUV",
                transmission = "Auto",
                fuel = "Benzin",
                dailyPrice = "₺2.950",
                totalPrice = "₺8.850",
                passengerCount = "5",
                bagCount = "3",
                tag = "POPULAR",
                imageResId = R.drawable.car_placeholder
            ),
            Vehicle(
                id = 4,
                name = "Toyota Corolla",
                type = "Hybrid Sedan",
                transmission = "Auto",
                fuel = "Hybrid",
                dailyPrice = "₺1.850",
                totalPrice = "₺5.550",
                passengerCount = "5",
                bagCount = "2",
                tag = "ÖNERİLEN",
                imageResId = R.drawable.car_placeholder
            ),
            Vehicle(
                id = 5,
                name = "Peugeot 3008",
                type = "SUV",
                transmission = "Auto",
                fuel = "Dizel",
                dailyPrice = "₺2.350",
                totalPrice = "₺7.050",
                passengerCount = "5",
                bagCount = "3",
                tag = "POPULAR",
                imageResId = R.drawable.car_placeholder
            )
        )
    }

    fun getInsurancePackages(): List<InsurancePackage> {
        return listOf(
            InsurancePackage(
                id = 1,
                name = "Mini Hasar Güvencesi",
                description = "Küçük çaplı hasarlar için ek koruma.",
                price = "₺149",
                dayText = "3 gün",
                isSelected = true
            ),
            InsurancePackage(
                id = 2,
                name = "Tam Koruma Paketi",
                description = "Daha kapsamlı güvence seçeneği.",
                price = "₺249",
                dayText = "3 gün",
                isSelected = false
            ),
            InsurancePackage(
                id = 3,
                name = "Ek Sürücü",
                description = "Aracı ikinci bir sürücü de kullanabilsin.",
                price = "₺99",
                dayText = "3 gün",
                isSelected = false
            )
        )
    }

    fun getExtraServices(): MutableList<ExtraService> {
        return mutableListOf(
            ExtraService(
                id = 1,
                name = "Bebek Koltuğu",
                description = "Çocuklu aileler için güvenli koltuk.",
                price = "₺75",
                quantity = 1
            ),
            ExtraService(
                id = 2,
                name = "Navigasyon",
                description = "Dahili veya taşınabilir navigasyon cihazı.",
                price = "₺60",
                quantity = 0
            )
        )
    }

}