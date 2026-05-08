package com.example.stocklocal.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocklocal.data.local.entity.Movement
import com.example.stocklocal.data.local.entity.Product
import com.example.stocklocal.data.repository.PreferencesRepository
import com.example.stocklocal.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val businessName = preferencesRepository.businessName

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _lowStockProducts = MutableStateFlow<List<Product>>(emptyList())
    val lowStockProducts: StateFlow<List<Product>> = _lowStockProducts

    private val _recentMovements = MutableStateFlow<List<Movement>>(emptyList())
    val recentMovements: StateFlow<List<Movement>> = _recentMovements

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            stockRepository.getAllProducts()
                .catch { }
                .collect { _products.value = it }
        }
        viewModelScope.launch {
            stockRepository.getLowStockProducts()
                .catch { }
                .collect { _lowStockProducts.value = it }
        }
        viewModelScope.launch {
            stockRepository.getAllMovements()
                .catch { }
                .collect { _recentMovements.value = it.take(5) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.clearPin()
        }
    }
}