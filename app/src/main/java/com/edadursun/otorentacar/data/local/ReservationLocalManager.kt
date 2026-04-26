package com.edadursun.otorentacar.data.local

import android.content.Context

// Kullanıcının oluşturduğu rezervasyon kodlarını telefonda saklamak için kullanılır
object ReservationLocalManager {

    private const val PREF_NAME = "otorentacar_reservations"
    private const val KEY_RESERVATION_CODES = "reservation_codes"

    // Yeni rezervasyon kodunu kaydeder
    // Aynı kod zaten varsa tekrar eklemez
    fun addReservationCode(context: Context, code: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentCodes = prefs.getStringSet(KEY_RESERVATION_CODES, emptySet())?.toMutableSet()
            ?: mutableSetOf()

        currentCodes.add(code)

        prefs.edit()
            .putStringSet(KEY_RESERVATION_CODES, currentCodes)
            .apply()
    }

    // Kayıtlı tüm rezervasyon kodlarını döndürür
    fun getReservationCodes(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_RESERVATION_CODES, emptySet())
            ?.toList()
            ?.sorted()
            ?: emptyList()
    }

    // Verilen rezervasyon kodunu listeden siler
    fun removeReservationCode(context: Context, code: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentCodes = prefs.getStringSet(KEY_RESERVATION_CODES, emptySet())?.toMutableSet()
            ?: mutableSetOf()

        currentCodes.remove(code)

        prefs.edit()
            .putStringSet(KEY_RESERVATION_CODES, currentCodes)
            .apply()
    }

    // Tüm kayıtlı rezervasyon kodlarını temizler
    fun clearAllReservationCodes(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_RESERVATION_CODES)
            .apply()
    }
}