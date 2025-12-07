package com.example.lab_mobile.todo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_operations")
data class PendingOperation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: String,
    val operationType: OperationType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OperationType {
    CREATE,
    UPDATE,
    DELETE
}