package com.example.lab_mobile.todo.ui.items

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab_mobile.todo.data.Item
import kotlinx.coroutines.delay

typealias OnItemFn = (id: String?) -> Unit

@Composable
fun ItemList(itemList: List<Item>, onItemClick: OnItemFn, modifier: Modifier) {
    Log.d("ItemList", "recompose")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(itemList) { item ->
            // animate each item visibility on appear
            AnimatedVisibility(visible = true, enter = fadeIn(animationSpec = tween(260))) {
                ItemDetail(item, onItemClick)
            }
        }
    }
}

@Composable
fun ItemDetail(item: Item, onItemClick: OnItemFn) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, animationSpec = tween(120))

    // reset pressed after a short delay when activated
    LaunchedEffect(pressed) {
        if (pressed) {
            delay(140)
            pressed = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable {
                pressed = true
                onItemClick(item._id)
            }
    ) {
        Text(
            text = item.name,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Release date: ${item.date}",
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = if (item.family_friendly) "✓ Family Friendly" else "✗ Not Family Friendly",
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = "ID: ${item._id}",
            style = TextStyle(fontSize = 12.sp),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
