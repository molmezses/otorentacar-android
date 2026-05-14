package com.edadursun.otorentacar.core.locale

import java.text.Normalizer
import java.util.Locale

object VehicleTextTranslator {

    private val englishTerms = mapOf(
        normalized("D\u00fcz Vites") to "Manual",
        normalized("Manuel") to "Manual",
        normalized("Manual") to "Manual",
        normalized("Otomatik") to "Automatic",
        normalized("Automatic") to "Automatic",
        normalized("Auto") to "Automatic",
        normalized("Benzin") to "Gasoline",
        normalized("Benzinli") to "Gasoline",
        normalized("Gasoline") to "Gasoline",
        normalized("Petrol") to "Gasoline",
        normalized("Dizel") to "Diesel",
        normalized("Diesel") to "Diesel",
        normalized("Elektrik") to "Electric",
        normalized("Elektrikli") to "Electric",
        normalized("Electric") to "Electric",
        normalized("Hibrit") to "Hybrid",
        normalized("Hybrid") to "Hybrid",
        normalized("Otomobil") to "Car",
        normalized("Araba") to "Car",
        normalized("Car") to "Car",
        normalized("Automobile") to "Car",
        normalized("Binek") to "Passenger Car",
        normalized("Binek Otomobil") to "Passenger Car",
        normalized("Passenger Car") to "Passenger Car",
        normalized("Ticari") to "Commercial",
        normalized("Commercial") to "Commercial",
        normalized("Hafif Ticari") to "Light Commercial",
        normalized("Light Commercial") to "Light Commercial",
        normalized("Transit") to "Van",
        normalized("Van") to "Van",
        normalized("Minib\u00fcs") to "Minibus",
        normalized("Minibus") to "Minibus",
        normalized("Panelvan") to "Panel Van",
        normalized("Panel Van") to "Panel Van",
        normalized("Sedan") to "Sedan",
        normalized("Hatchback") to "Hatchback",
        normalized("SUV") to "SUV",
        normalized("Ekonomik") to "Economy",
        normalized("Ekonomi") to "Economy",
        normalized("Economy") to "Economy",
        normalized("Orta Segment") to "Mid-size",
        normalized("Mid-size") to "Mid-size",
        normalized("\u00dcst Segment") to "Premium",
        normalized("Premium") to "Premium",
        normalized("L\u00fcks") to "Luxury",
        normalized("Luxury") to "Luxury",
        normalized("Kompakt") to "Compact",
        normalized("Compact") to "Compact"
    )

    private val turkishTerms = mapOf(
        normalized("Manual") to "D\u00fcz Vites",
        normalized("D\u00fcz Vites") to "D\u00fcz Vites",
        normalized("Manuel") to "D\u00fcz Vites",
        normalized("Automatic") to "Otomatik",
        normalized("Auto") to "Otomatik",
        normalized("Otomatik") to "Otomatik",
        normalized("Gasoline") to "Benzin",
        normalized("Petrol") to "Benzin",
        normalized("Benzin") to "Benzin",
        normalized("Benzinli") to "Benzin",
        normalized("Diesel") to "Dizel",
        normalized("Dizel") to "Dizel",
        normalized("Electric") to "Elektrikli",
        normalized("Elektrik") to "Elektrikli",
        normalized("Elektrikli") to "Elektrikli",
        normalized("Hybrid") to "Hibrit",
        normalized("Hibrit") to "Hibrit",
        normalized("Car") to "Otomobil",
        normalized("Automobile") to "Otomobil",
        normalized("Otomobil") to "Otomobil",
        normalized("Araba") to "Otomobil",
        normalized("Passenger Car") to "Binek Otomobil",
        normalized("Binek") to "Binek Otomobil",
        normalized("Binek Otomobil") to "Binek Otomobil",
        normalized("Commercial") to "Ticari",
        normalized("Ticari") to "Ticari",
        normalized("Light Commercial") to "Hafif Ticari",
        normalized("Hafif Ticari") to "Hafif Ticari",
        normalized("Van") to "Transit",
        normalized("Transit") to "Transit",
        normalized("Minibus") to "Minib\u00fcs",
        normalized("Minib\u00fcs") to "Minib\u00fcs",
        normalized("Panel Van") to "Panelvan",
        normalized("Panelvan") to "Panelvan",
        normalized("Sedan") to "Sedan",
        normalized("Hatchback") to "Hatchback",
        normalized("SUV") to "SUV",
        normalized("Economy") to "Ekonomik",
        normalized("Ekonomik") to "Ekonomik",
        normalized("Ekonomi") to "Ekonomik",
        normalized("Mid-size") to "Orta Segment",
        normalized("Orta Segment") to "Orta Segment",
        normalized("Premium") to "\u00dcst Segment",
        normalized("\u00dcst Segment") to "\u00dcst Segment",
        normalized("Luxury") to "L\u00fcks",
        normalized("L\u00fcks") to "L\u00fcks",
        normalized("Compact") to "Kompakt",
        normalized("Kompakt") to "Kompakt"
    )

    fun translate(text: String): String {
        if (text.isBlank()) return text

        val activeTerms = if (Locale.getDefault().language == LocaleHelper.ENGLISH) {
            englishTerms
        } else {
            turkishTerms
        }

        val parts = text.split("|")
        return parts.joinToString(" | ") { part ->
            translateSingle(part.trim(), activeTerms)
        }
    }

    private fun translateSingle(text: String, terms: Map<String, String>): String {
        val exact = terms[normalized(text)]
        if (exact != null) return applyCasePattern(text, exact)

        return text
            .split(Regex("\\s+"))
            .joinToString(" ") { word ->
                val translated = terms[normalized(word)] ?: word
                applyCasePattern(word, translated)
            }
    }

    private fun applyCasePattern(source: String, translated: String): String {
        val letterChars = source.filter { it.isLetter() }
        if (letterChars.isEmpty()) return translated

        val targetLocale = if (Locale.getDefault().language == LocaleHelper.ENGLISH) {
            Locale.ENGLISH
        } else {
            Locale.forLanguageTag("tr-TR")
        }

        return when {
            letterChars.all { it.isUpperCase() } -> translated.uppercase(targetLocale)
            letterChars.all { it.isLowerCase() } -> translated.lowercase(targetLocale)
            else -> translated
        }
    }

    private fun normalized(text: String): String {
        val withoutAccents = Normalizer
            .normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")

        return withoutAccents
            .replace("\u0131", "i")
            .replace("\u0130", "I")
            .lowercase(Locale.ROOT)
            .trim()
    }
}
