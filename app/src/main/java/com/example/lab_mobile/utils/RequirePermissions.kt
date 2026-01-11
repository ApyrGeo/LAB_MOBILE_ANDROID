package com.example.lab_mobile.utils

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequirePermissions(
    permissions: List<String>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    when {
        permissionState.allPermissionsGranted -> {
            content()
        }
        else -> {
            PermissionRequestScreen(permissionState = permissionState, modifier = modifier)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRequestScreen(
    permissionState: MultiplePermissionsState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        val textToShow = if (permissionState.shouldShowRationale) {
            "Location permissions are required for this feature. Please grant the permissions."
        } else {
            "Location permissions are required. Please grant the permissions to continue."
        }

        Text(text = textToShow)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
            Text("Request Permissions")
        }
    }
}

