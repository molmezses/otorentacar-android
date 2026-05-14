package com.edadursun.otorentacar.core.locale

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {

    private const val PREF_NAME = "otorentacar_locale"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_DEFAULT_LANGUAGE_INITIALIZED = "default_language_initialized"
    const val TURKISH = "tr"
    const val ENGLISH = "en"

    fun wrap(context: Context): Context {
        ensureDefaultLanguage(context)

        val locale = Locale(getLanguage(context))
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }

    fun getLanguage(context: Context): String {
        ensureDefaultLanguage(context)

        val storedLanguage = context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, null)

        if (storedLanguage == TURKISH || storedLanguage == ENGLISH) {
            return storedLanguage
        }

        return TURKISH
    }

    fun setLanguage(context: Context, language: String) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .putBoolean(KEY_DEFAULT_LANGUAGE_INITIALIZED, true)
            .apply()
    }

    fun toggleLanguage(context: Context): String {
        val nextLanguage = if (getLanguage(context) == TURKISH) ENGLISH else TURKISH
        setLanguage(context, nextLanguage)
        return nextLanguage
    }

    private fun ensureDefaultLanguage(context: Context) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (preferences.getBoolean(KEY_DEFAULT_LANGUAGE_INITIALIZED, false)) return

        preferences
            .edit()
            .putString(KEY_LANGUAGE, TURKISH)
            .putBoolean(KEY_DEFAULT_LANGUAGE_INITIALIZED, true)
            .apply()
    }
}
