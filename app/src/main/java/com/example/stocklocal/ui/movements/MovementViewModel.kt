package com.example.stocklocal.ui.movements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocklocal.data.local.entity.Movement
import com.example.stocklocal.data.local.entity.Product
import com.example.stocklocal.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovementViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovementUiState>(MovementUiState.Loading)
    val uiState: StateFlow<MovementUiState> = _uiState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadMovements()
    }

    private fun loadMovements() {
        viewModelScope.launch {
            repository.getAllMovements()
                .catch { _uiState.value = MovementUiState.Error("Error al cargar movimientos") }
                .collect { movements -> _uiState.value = MovementUiState.Success(movements) }
        }
    }

    fun registerMovement(product: Product, type: String, quantity: Int) {
        viewModelScope.launch {
            try {
                if (type == "Salida" && quantity > product.quantity) {
                    _errorMessage.value =
                        "No hay suficiente stock. Disponible: ${product.quantity} unidades"
                    return@launch
                }
                val movement = Movement(
                    productId = product.id,
                    productName = product.name,
                    type = type,
                    quantity = quantity
                )
                repository.insertMovement(movement)
                val newQuantity = if (type == "Entrada") {
                    product.quantity + quantity
                } else {
                    product.quantity - quantity
                }
                repository.updateProduct(product.copy(quantity = newQuantity))
            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar movimiento"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

sealed class MovementUiState {
    object Loading : MovementUiState()
    data class Success(val movements: List<Movement>) : MovementUiState()
    data class Error(val message: String) : MovementUiState()
}