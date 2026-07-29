package com.st10445368.gitbbc.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class LocationCountryResult(
    val latitude: Double,
    val longitude: Double,
    val countryCode: String,  // e.g., "za"
    val countryName: String   // e.g., "South Africa"
)

class CountryGeocoder(private val context: Context) {

    private val geocoder = Geocoder(context, Locale.getDefault())

    /**
     * Converts latitude & longitude into ISO country code and country name.
     * Uses asynchronous API on Android 13+ (API 33+) and fallback for older versions.
     */
    suspend fun getCountryFromCoordinates(lat: Double, lng: Double): LocationCountryResult? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        val address = addresses.firstOrNull()
                        val countryCode = address?.countryCode?.lowercase()
                        val countryName = address?.countryName ?: "Unknown"

                        if (!countryCode.isNullOrEmpty()) {
                            continuation.resume(
                                LocationCountryResult(lat, lng, countryCode, countryName)
                            )
                        } else {
                            continuation.resume(null)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        continuation.resume(null)
                    }
                })
            }
        } else {
            // Synchronous fallback for Android 12 (API 31) and lower
            @Suppress("DEPRECATION")
            try {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val address = addresses?.firstOrNull()
                val countryCode = address?.countryCode?.lowercase()
                val countryName = address?.countryName ?: "Unknown"

                if (!countryCode.isNullOrEmpty()) {
                    LocationCountryResult(lat, lng, countryCode, countryName)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}