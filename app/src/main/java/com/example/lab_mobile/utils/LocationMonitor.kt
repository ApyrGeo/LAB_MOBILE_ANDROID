package com.example.lab_mobile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationMonitor(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    val currentLocation: Flow<Location?> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    Log.d("LocationMonitor", "New location: ${location.latitude}, ${location.longitude}")
                    trySend(location)
                }
            }
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 seconds
        ).apply {
            setMinUpdateIntervalMillis(5000L) // 5 seconds
            setMaxUpdateDelayMillis(10000L)
        }.build()

        try {
            // Get last known location first
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    Log.d("LocationMonitor", "Last known location: ${it.latitude}, ${it.longitude}")
                    trySend(it)
                }
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("LocationMonitor", "Location permission not granted", e)
            close(e)
        }

        awaitClose {
            Log.d("LocationMonitor", "Removing location updates")
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

