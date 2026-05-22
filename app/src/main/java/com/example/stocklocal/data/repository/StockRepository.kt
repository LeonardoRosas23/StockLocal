package com.example.stocklocal.data.repository

import com.example.stocklocal.data.local.dao.MovementDao
import com.example.stocklocal.data.local.dao.ProductDao
import com.example.stocklocal.data.local.entity.Movement
import com.example.stocklocal.data.local.entity.Product
import com.example.stocklocal.data.remote.api.OpenFoodApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import com.example.stocklocal.di.TokenProvider

@Singleton
class StockRepository @Inject constructor(
    private val productDao: ProductDao,
    private val movementDao: MovementDao,
    private val apiService: OpenFoodApiService,
    private val preferencesRepository: PreferencesRepository,
    private val tokenProvider: TokenProvider
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
    suspend fun loginAndSaveToken(): Boolean {
        return try {
            val response = apiService.login(
                com.example.stocklocal.data.remote.model.LoginRequest(
                    username = "emilys",
                    password = "emilyspass"
                )
            )
            val token = response.accessToken
            if (token != null) {
                preferencesRepository.saveToken(token)
                tokenProvider.token = token
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun searchProductOnline(productName: String): String? {
        return try {
            val response = apiService.searchProduct(productName)
            response.products?.firstOrNull()?.category
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteAllData() {
        productDao.deleteAllProducts()
        movementDao.deleteAllMovements()
    }
}