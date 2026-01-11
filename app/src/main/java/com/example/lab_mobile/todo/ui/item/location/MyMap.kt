package com.example.lab_mobile.todo.ui.item.location

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

val TAG = "MyMap"

@Composable
fun MyMap(
    lat: Double,
    long: Double,
    modifier: Modifier,
    onLocationSelected: (Double, Double) -> Unit = { _, _ -> }
) {
    val markerState = rememberMarkerState(position = LatLng(lat, long))
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    // Update camera when initial location changes
    LaunchedEffect(lat, long) {
        markerState.position = LatLng(lat, long)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, long), 10f)
    }

    // Call callback when marker position changes
    LaunchedEffect(markerState.position) {
        onLocationSelected(markerState.position.latitude, markerState.position.longitude)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = {
            Log.d(TAG, "onMapClick $it")
            markerState.position = it
        },
        onMapLongClick = {
            Log.d(TAG, "onMapLongClick $it")
            markerState.position = it
        },
    ) {
        Marker(
            state = markerState,
            title = "Selected location",
            snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}",
            draggable = true
        )
    }
}
