package com.example.lab_mobile.todo.ui.items

import android.app.Application
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab_mobile.R
import com.example.lab_mobile.utils.ui.MyNetworkStatus
import com.example.lab_mobile.utils.ui.SyncStatusCard
import com.example.lab_mobile.utils.ui.SyncStatusViewModel
import com.example.myapp3.ProximityColorViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(onItemClick: (id: String?) -> Unit, onAddItem: () -> Unit, onLogout: () -> Unit) {
    Log.d("ItemsScreen", "recompose")
    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = listOf()
    )

    // Proximity sensor for background color change
    val proximityViewModel = viewModel<ProximityColorViewModel>(
        factory = ProximityColorViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
    val proximityDistance = proximityViewModel.proximityDistance

    // Calculate background color: white when far (1.0), blue when close (0.0)
    val backgroundColor = lerp(
        start = Color(0xFF344BC4), // Blue color when close
        stop = Color.White,         // White color when far
        fraction = proximityDistance
    )

    // Sync status viewmodel - used to show a transient banner when sync starts
    val syncViewModel = viewModel<SyncStatusViewModel>(
        factory = SyncStatusViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
    val syncUiState by syncViewModel.uiState.collectAsStateWithLifecycle()

    // transient banner state
    var showTransientSync by remember { mutableStateOf(false) }
    LaunchedEffect(syncUiState.isSyncing) {
        if (syncUiState.isSyncing) {
            showTransientSync = true
            // keep visible for 5 seconds
            delay(5000)
            showTransientSync = false
        }
    }

    // screen enter animation toggle
    var screenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { screenVisible = true }

    // FAB press animation
    var fabPressed by remember { mutableStateOf(false) }
    val fabScale by animateFloatAsState(targetValue = if (fabPressed) 0.92f else 1f, animationSpec = tween(160))

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.items)) },
                    actions = {
                        Button(onClick = onLogout) { Text("Logout") }
                    }
                )
                MyNetworkStatus()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // small press animation before navigating
                    fabPressed = true
                    onAddItem()
                },
                modifier = Modifier
                    .then(Modifier)
                    .graphicsLayer(scaleX = fabScale, scaleY = fabScale)
            ) {
                Icon(Icons.Rounded.Add, "Add")
            }
        }
    ) { innerPadding ->
        AnimatedVisibility(
            visible = screenVisible,
            enter = fadeIn(animationSpec = tween(2000)) + slideInVertically(animationSpec = tween(900)) { it },
            exit = fadeOut(animationSpec = tween(2000)) + slideOutVertically(animationSpec = tween(900)) { -it }
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                // transient sync banner
                AnimatedVisibility(
                    visible = showTransientSync,
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically { -it },
                    exit = fadeOut(animationSpec = tween(800)) + slideOutVertically { -it }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF176)) // bright yellow highlight
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Sync in progress...",
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                SyncStatusCard()

                ItemList(
                    itemList = itemsUiState,
                    onItemClick = onItemClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewItemsScreen() {
    ItemsScreen(onItemClick = {}, onAddItem = {}, onLogout = {})
}
