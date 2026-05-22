package com.example.stocklocal.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class InventoryViewModel @Inject constructor(
    private val repository: StockRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val currency = preferencesRepository.currency

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState

    private val _suggestedCategory = MutableStateFlow<String?>(null)
    val suggestedCategory: StateFlow<String?> = _suggestedCategory

    private val _categorySearchMessage = MutableStateFlow<String?>(null)
    val categorySearchMessage: StateFlow<String?> = _categorySearchMessage

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .catch {
                    _uiState.value = InventoryUiState.Error("Error al cargar productos")
                    _message.value = "No se pudo cargar el inventario"
                }
                .collect { products ->
                    _uiState.value = InventoryUiState.Success(products)
                }
        }
    }

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.insertProduct(product)
                _message.value = "Producto agregado correctamente"
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error("Error al guardar producto")
                _message.value = "No se pudo guardar el producto"
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
                _message.value = "Producto actualizado correctamente"
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error("Error al actualizar producto")
                _message.value = "No se pudo actualizar el producto"
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product)
                _message.value = "Producto eliminado correctamente"
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error("Error al eliminar producto")
                _message.value = "No se pudo eliminar el producto"
            }
        }
    }

    fun searchCategoryOnline(productName: String) {
        viewModelScope.launch {
            _categorySearchMessage.value = null

            try {
                val category = repository.searchProductOnline(productName)

                if (category.isNullOrBlank()) {
                    _categorySearchMessage.value =
                        "No se encontró una categoría automática. Puedes ingresarla manualmente."
                } else {
                    _suggestedCategory.value = category
                    _categorySearchMessage.value = "Categoría sugerida aplicada."
                }
            } catch (e: Exception) {
                _categorySearchMessage.value =
                    "No se pudo obtener la categoría automática. Puedes ingresarla manualmente."
            }
        }
    }

    fun clearSuggestedCategory() {
        _suggestedCategory.value = null
        _categorySearchMessage.value = null
    }

    fun clearMessage() {
        _message.value = null
    }
}

sealed class InventoryUiState {
    object Loading : InventoryUiState()
    data class Success(val products: List<Product>) : InventoryUiState()
    data class Error(val message: String) : InventoryUiState()
}