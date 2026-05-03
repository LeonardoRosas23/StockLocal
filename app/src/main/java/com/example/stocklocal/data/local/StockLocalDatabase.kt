package com.example.stocklocal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stocklocal.data.local.dao.MovementDao
import com.example.stocklocal.data.local.dao.ProductDao
import com.example.stocklocal.data.local.entity.Movement
import com.example.stocklocal.data.local.entity.Product

@Database(
    entities = [Product::class, Movement::class],
    version = 1,
    exportSchema = false
)
abstract class StockLocalDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun movementDao(): MovementDao
}