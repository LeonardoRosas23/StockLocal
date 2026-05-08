package com.example.stocklocal.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocklocal.data.local.entity.Product
import com.example.stocklocal.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.stocklocal.data.repository.PreferencesRepository

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: StockRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val currency = preferencesRepository.currency

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState

    private val _suggestedCategory = MutableStateFlow<String?>(null)
    val suggestedCategory: StateFlow<String?> = _suggestedCategory

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .catch { _uiState.value = InventoryUiState.Error("Error al cargar productos") }
                .collect { products -> _uiState.value = InventoryUiState.Success(products) }
        }
    }

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.insertProduct(product)
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error("Error al guardar producto")
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error("Error al actualizar producto")
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product)
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error("Error al eliminar producto")
            }
        }
    }

    fun searchCategoryOnline(productName: String) {
        viewModelScope.launch {
            _suggestedCategory.value = repository.searchProductOnline(productName)
        }
    }

    fun clearSuggestedCategory() {
        _suggestedCategory.value = null
    }
}

sealed class InventoryUiState {
    object Loading : InventoryUiState()
    data class Success(val products: List<Product>) : InventoryUiState()
    data class Error(val message: String) : InventoryUiState()
}