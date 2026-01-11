package com.example.lab_mobile.todo.ui.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab_mobile.core.Result
import com.example.lab_mobile.todo.ui.item.location.MyLocation
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val IsoUtcFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)

fun Long.toIsoUtcString(): String = IsoUtcFormatter.format(Instant.ofEpochMilli(this))
fun String.toEpochMillisOrNull(): Long? = runCatching { Instant.parse(this).toEpochMilli() }.getOrNull()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(itemId: String?, onClose: () -> Unit) {
    val itemViewModel = viewModel<ItemViewModel>(factory = ItemViewModel.Factory(itemId))
    val itemUiState = itemViewModel.uiState

    var name by rememberSaveable { mutableStateOf(itemUiState.item.name) }
    var family_friendly by rememberSaveable { mutableStateOf(itemUiState.item.family_friendly) }
    var nr_players by rememberSaveable { mutableStateOf(itemUiState.item.nr_players) }
    var date by rememberSaveable { mutableStateOf(itemUiState.item.date) }
    var latitude by rememberSaveable { mutableStateOf(itemUiState.item.latitude) }
    var longitude by rememberSaveable { mutableStateOf(itemUiState.item.longitude) }

    LaunchedEffect(itemUiState.submitResult) {
        if (itemUiState.submitResult is Result.Success) onClose()
    }

    var initialized by remember { mutableStateOf(itemId == null) }
    LaunchedEffect(itemId, itemUiState.loadResult) {
        if (initialized) return@LaunchedEffect
        if (itemUiState.loadResult !is Result.Loading) {
            name = itemUiState.item.name
            family_friendly = itemUiState.item.family_friendly
            nr_players = itemUiState.item.nr_players
            date = itemUiState.item.date
            latitude = itemUiState.item.latitude
            longitude = itemUiState.item.longitude
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item") },
                actions = {
                    Button(onClick = {
                        itemViewModel.saveOrUpdateItem(
                            name = name,
                            nr_players = nr_players,
                            date = date, // already in ISO 8601 UTC format
                            family_friendly = family_friendly,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }) { Text("Save") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (itemUiState.loadResult is Result.Loading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }
            } else {
                if (itemUiState.submitResult is Result.Loading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { LinearProgressIndicator() }
                }
                if (itemUiState.loadResult is Result.Error) {
                    Text("Failed to load item - ${(itemUiState.loadResult as Result.Error).exception?.message}")
                }

                // Animated Name TextField
                val nameInteractionSource = remember { MutableInteractionSource() }
                val isNameFocused by nameInteractionSource.collectIsFocusedAsState()
                val nameScale = remember { Animatable(1f) }

                LaunchedEffect(isNameFocused) {
                    nameScale.animateTo(
                        targetValue = if (isNameFocused) 1.02f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .graphicsLayer(
                            scaleX = nameScale.value,
                            scaleY = nameScale.value,
                            shadowElevation = if (isNameFocused) 8f else 0f
                        )
                ) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        interactionSource = nameInteractionSource
                    )
                }

                // Animated Nr Players TextField
                val nrPlayersInteractionSource = remember { MutableInteractionSource() }
                val isNrPlayersFocused by nrPlayersInteractionSource.collectIsFocusedAsState()
                val nrPlayersScale = remember { Animatable(1f) }

                LaunchedEffect(isNrPlayersFocused) {
                    nrPlayersScale.animateTo(
                        targetValue = if (isNrPlayersFocused) 1.02f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .graphicsLayer(
                            scaleX = nrPlayersScale.value,
                            scaleY = nrPlayersScale.value,
                            shadowElevation = if (isNrPlayersFocused) 8f else 0f
                        )
                ) {
                    TextField(
                        value = nr_players.toString(),
                        onValueChange = {
                            nr_players = it.toIntOrNull() ?: nr_players
                        },
                        label = { Text("Nr Players") },
                        interactionSource = nrPlayersInteractionSource
                    )
                }

                // Animated DatePicker
                var datePickerVisible by remember { mutableStateOf(true) }
                val datePickerScale = remember { Animatable(1f) }

                LaunchedEffect(Unit) {
                    datePickerVisible = true
                }

                AnimatedVisibility(
                    visible = datePickerVisible,
                    enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                        animationSpec = tween(400),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .animateContentSize()
                            .graphicsLayer(scaleX = datePickerScale.value, scaleY = datePickerScale.value)
                    ) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = date.toEpochMillisOrNull()
                        )
                        LaunchedEffect(datePickerState.selectedDateMillis) {
                            datePickerState.selectedDateMillis?.let { millis ->
                                date = millis.toIsoUtcString()
                                // Animate on selection
                                datePickerScale.animateTo(1.05f, animationSpec = tween(100))
                                datePickerScale.animateTo(1f, animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                ))
                            }
                        }

                        DatePicker(
                            state = datePickerState,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Animated RadioButton
                val radioButtonScale = remember { Animatable(1f) }
                val coroutineScope = rememberCoroutineScope()

                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .graphicsLayer(
                            scaleX = radioButtonScale.value,
                            scaleY = radioButtonScale.value
                        )
                ) {
                    RadioButton(
                        selected = family_friendly,
                        onClick = {
                            family_friendly = !family_friendly
                            coroutineScope.launch {
                                radioButtonScale.animateTo(1.15f, animationSpec = tween(100))
                                radioButtonScale.animateTo(1f, animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ))
                            }
                        }
                    )
                    Text(
                        text = "Family Friendly",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Location Section with animation
                var locationVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(200) // Slight delay for staggered animation
                    locationVisible = true
                }

                AnimatedVisibility(
                    visible = locationVisible,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(
                            text = "Location",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (latitude != null && longitude != null) {
                            Text(
                                text = "Lat: $latitude, Lng: $longitude",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        MyLocation(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            initialLatitude = latitude,
                            initialLongitude = longitude,
                            onLocationSelected = { lat, lng ->
                                latitude = lat
                                longitude = lng
                            }
                        )
                    }
                }

                if (itemUiState.submitResult is Result.Error) {
                    Text(
                        text = "Failed to submit item - ${(itemUiState.submitResult as Result.Error).exception?.message}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}



@Preview
@Composable
fun PreviewItemScreen() {
    ItemScreen(itemId = "0", onClose = {})
}
