package com.example.stocklocal.data.repository

import com.example.stocklocal.data.local.dao.MovementDao
import com.example.stocklocal.data.local.dao.ProductDao
import com.example.stocklocal.data.local.entity.Movement
import com.example.stocklocal.data.local.entity.Product
import com.example.stocklocal.data.remote.api.OpenFoodApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val productDao: ProductDao,
    private val movementDao: MovementDao,
    private val apiService: OpenFoodApiService
) {
    // Productos
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    fun getLowStockProducts(): Flow<List<Product>> = productDao.getLowStockProducts()

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    // Movimientos
    fun getAllMovements(): Flow<List<Movement>> = movementDao.getAllMovements()

    fun getMovementsByProduct(productId: Int): Flow<List<Movement>> =
        movementDao.getMovementsByProduct(productId)

    suspend fun insertMovement(movement: Movement) = movementDao.insertMovement(movement)

    // API
    suspend fun searchProductOnline(productName: String): String? {
        return try {
            val response = apiService.searchProduct(productName)
            val firstProduct = response.products?.firstOrNull()
            firstProduct?.category?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteAllData() {
        productDao.deleteAllProducts()
        movementDao.deleteAllMovements()
    }
}