package com.example.stocklocal.di

import android.content.Context
import androidx.room.Room
import com.example.stocklocal.data.local.StockLocalDatabase
import com.example.stocklocal.data.local.dao.MovementDao
import com.example.stocklocal.data.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StockLocalDatabase {
        return Room.databaseBuilder(
            context,
            StockLocalDatabase::class.java,
            "stocklocal_database"
        ).build()
    }

    @Provides
    fun provideProductDao(database: StockLocalDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideMovementDao(database: StockLocalDatabase): MovementDao {
        return database.movementDao()
    }
}