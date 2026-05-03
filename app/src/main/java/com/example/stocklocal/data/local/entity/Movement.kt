package com.example.stocklocal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movements")
data class Movement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val productName: String,
    val type: String,
    val quantity: Int,
    val date: Long = System.currentTimeMillis()
)