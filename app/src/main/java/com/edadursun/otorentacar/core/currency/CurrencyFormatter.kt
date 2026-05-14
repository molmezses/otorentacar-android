package com.edadursun.otorentacar.core.currency

import java.util.Locale
import kotlin.math.roundToInt

object CurrencyFormatter {
    const val EUR_TO_TRY_RATE = 53.2137

    fun format(
        amount: Double,
        sourceCurrencyCode: String,
        displayCurrency: DisplayCurrency
    ): String {
        val displayAmount = convertAmount(amount, sourceCurrencyCode, displayCurrency)

        return when (displayCurrency) {
            DisplayCurrency.EURO -> "\u20ac${formatAmount(displayAmount)}"
            DisplayCurrency.TL -> "\u20ba${formatTl(displayAmount)}"
        }
    }

    fun convertAmount(
        amount: Double,
        sourceCurrencyCode: String,
        displayCurrency: DisplayCurrency
    ): Double {
        val sourceCurrency = sourceCurrencyCode.uppercase(Locale.ROOT)
        val isSourceTry = sourceCurrency == "TRY" || sourceCurrency == "TL"
        return when (displayCurrency) {
            DisplayCurrency.EURO -> if (isSourceTry) amount / EUR_TO_TRY_RATE else amount
            DisplayCurrency.TL -> if (isSourceTry) amount else amount * EUR_TO_TRY_RATE
        }
    }

    fun currencyCodeFor(displayCurrency: DisplayCurrency): String {
        return when (displayCurrency) {
            DisplayCurrency.EURO -> "EUR"
            DisplayCurrency.TL -> "TRY"
        }
    }

    private fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", amount)
        }
    }

    private fun formatTl(amount: Double): String {
        val rounded = amount.roundToInt()
        return "%,d".format(Locale.forLanguageTag("tr-TR"), rounded)
    }
}
