package com.st10445368.gitbbc.models


data class Country(
    val code: String,
    val name: String,
    val flagEmoji: String
)

object CountryData {
    val supportedCountries = listOf(
        Country("za", "South Africa", "🇿🇦"),
        Country("us", "United States", "🇺🇸"),
        Country("gb", "United Kingdom", "🇬🇧"),
        Country("ca", "Canada", "🇨🇦"),
        Country("au", "Australia", "🇦🇺"),
        Country("in", "India", "🇮🇳"),
        Country("de", "Germany", "🇩🇪"),
        Country("fr", "France", "🇫🇷"),
        Country("jp", "Japan", "🇯🇵"),
        Country("br", "Brazil", "🇧🇷")
    )

    fun getCountryName(code: String): String {
        return supportedCountries.firstOrNull { it.code.equals(code, ignoreCase = true) }?.name
            ?: code.uppercase()
    }
}