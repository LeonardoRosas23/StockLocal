package com.example.stocklocal.data.local.dao

import androidx.room.*
import com.example.stocklocal.data.local.entity.Movement
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {

    @Query("SELECT * FROM movements ORDER BY date DESC")
    fun getAllMovements(): Flow<List<Movement>>

    @Query("SELECT * FROM movements WHERE productId = :productId ORDER BY date DESC")
    fun getMovementsByProduct(productId: Int): Flow<List<Movement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: Movement)
}