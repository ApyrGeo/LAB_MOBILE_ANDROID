package com.example.lab_mobile.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey val _id: String = "",
    val name: String = "",
    val nr_players: Int = 0,
    val date: String = "",
    val family_friendly: Boolean = false,
    val version: Int = 0,
    var needsSync: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)