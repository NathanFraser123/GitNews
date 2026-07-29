package com.st10445368.gitbbc.location


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Retrieves the current device location (latitude and longitude).
     * Assumes location permissions have already been checked at runtime.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                continuation.resume(location)
            } else {
                // Fallback to last known location if immediate fix is null
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { lastLoc -> continuation.resume(lastLoc) }
                    .addOnFailureListener { continuation.resume(null) }
            }
        }.addOnFailureListener {
            continuation.resume(null)
        }

        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }
}