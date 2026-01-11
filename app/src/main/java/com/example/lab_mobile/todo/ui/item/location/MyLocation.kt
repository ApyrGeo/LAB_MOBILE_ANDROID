package com.example.lab_mobile.todo.ui.item.location

import android.Manifest
import android.app.Application
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab_mobile.utils.RequirePermissions

@Composable
fun MyLocation(
    modifier: Modifier = Modifier,
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    onLocationSelected: (Double, Double) -> Unit = { _, _ -> }
) {
    RequirePermissions(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        modifier = modifier
    ) {
        ShowMyLocation(
            modifier = modifier,
            initialLatitude = initialLatitude,
            initialLongitude = initialLongitude,
            onLocationSelected = onLocationSelected
        )
    }
}

@Composable
fun ShowMyLocation(
    modifier: Modifier,
    initialLatitude: Double?,
    initialLongitude: Double?,
    onLocationSelected: (Double, Double) -> Unit
) {
    val myLocationViewModel = viewModel<MyLocationViewModel>(
        factory = MyLocationViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )

    val currentLocation = myLocationViewModel.uiState

    // Use initial coordinates if available, otherwise use current location
    val lat: Double?
    val lng: Double?

    if (initialLatitude != null && initialLongitude != null) {
        lat = initialLatitude
        lng = initialLongitude
    } else if (currentLocation != null) {
        lat = currentLocation.latitude
        lng = currentLocation.longitude
    } else {
        lat = null
        lng = null
    }

    if (lat != null && lng != null) {
        MyMap(
            lat = lat,
            long = lng,
            modifier = modifier,
            onLocationSelected = onLocationSelected
        )
    } else {
        LinearProgressIndicator()
    }
}
