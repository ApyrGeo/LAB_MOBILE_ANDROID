package com.example.lab_mobile.todo.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab_mobile.R
import com.example.lab_mobile.core.Result
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.toString

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
                            family_friendly = family_friendly
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

                Row {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row {
                    TextField(
                        value = nr_players.toString(),
                        onValueChange = {
                            nr_players = it.toIntOrNull() ?: nr_players
                        },
                        label = { Text("Nr Players") }
                    )
                }

                Row {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = date.toEpochMillisOrNull()
                    )
                    LaunchedEffect(datePickerState.selectedDateMillis) {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = millis.toIsoUtcString()
                        }
                    }

                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row {
                    RadioButton(
                        selected = family_friendly,
                        onClick = { family_friendly = !family_friendly }
                    )
                    Text(
                        text = "Family Friendly",
                        modifier = Modifier.padding(start = 8.dp)
                    )
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
