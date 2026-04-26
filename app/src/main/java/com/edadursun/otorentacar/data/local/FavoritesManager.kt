package com.edadursun.otorentacar.data.local

import com.edadursun.otorentacar.data.model.Vehicle

// Uygulama açık olduğu sürece favori araçları geçici olarak bellekte tutar
object FavoritesManager {

    // Favoriye eklenen araçların tutulduğu liste
    private val favoriteVehicles = mutableListOf<Vehicle>()

    // Tüm favori araçları dışarıya sadece okunabilir liste olarak verir
    fun getFavorites(): List<Vehicle> = favoriteVehicles.toList()

    // Aracı favorilere ekler, eğer zaten varsa tekrar eklemez
    fun addToFavorites(vehicle: Vehicle) {
        val exists = favoriteVehicles.any { it.id == vehicle.id }
        if (!exists) {
            favoriteVehicles.add(vehicle)
        }
    }

    // Verilen id'ye sahip aracı favorilerden kaldırır
    fun removeFromFavorites(vehicleId: Int) {
        favoriteVehicles.removeAll { it.id == vehicleId }
    }

    // Aracın favorilerde olup olmadığını kontrol eder
    fun isFavorite(vehicleId: Int): Boolean {
        return favoriteVehicles.any { it.id == vehicleId }
    }

    // Eğer araç favorideyse çıkarır, değilse ekler
    // Geriye yeni favori durumunu döndürür:
    // true = favorilere eklendi
    // false = favorilerden çıkarıldı
    fun toggleFavorite(vehicle: Vehicle): Boolean {
        return if (isFavorite(vehicle.id)) {
            removeFromFavorites(vehicle.id)
            false
        } else {
            addToFavorites(vehicle)
            true
        }
    }
}