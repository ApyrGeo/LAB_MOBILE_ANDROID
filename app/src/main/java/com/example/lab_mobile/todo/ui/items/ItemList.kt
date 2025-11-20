package com.example.lab_mobile.todo.ui.items

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab_mobile.todo.data.Item

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
            ItemDetail(item, onItemClick)
        }
    }
}

@Composable
fun ItemDetail(item: Item, onItemClick: OnItemFn) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        ClickableText(
            text = AnnotatedString(item.name),
            style = TextStyle(
                fontSize = 24.sp,
            ),
            onClick = { onItemClick(item._id) }
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
            text = "Price: $${item.date}",
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

