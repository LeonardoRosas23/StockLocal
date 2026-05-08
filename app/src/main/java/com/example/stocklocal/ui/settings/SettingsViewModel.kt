package com.example.stocklocal.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocklocal.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val stockRepository: com.example.stocklocal.data.repository.StockRepository
) : ViewModel() {

    val businessName = preferencesRepository.businessName
    val currency = preferencesRepository.currency

    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val settingsState: StateFlow<SettingsState> = _settingsState

    fun savePin(pin: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.savePin(pin)
                _settingsState.value = SettingsState.Success("PIN guardado correctamente")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al guardar el PIN")
            }
        }
    }

    fun saveBusinessName(name: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.saveBusinessName(name)
                _settingsState.value = SettingsState.Success("Nombre guardado correctamente")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al guardar el nombre")
            }
        }
    }

    fun saveCurrency(currency: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.saveCurrency(currency)
                _settingsState.value = SettingsState.Success("Moneda guardada correctamente")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al guardar la moneda")
            }
        }
    }

    fun resetState() {
        _settingsState.value = SettingsState.Idle
    }

    fun clearPin() {
        viewModelScope.launch {
            try {
                preferencesRepository.clearPin()
                _settingsState.value = SettingsState.Success("Sesión cerrada")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al cerrar sesión")
            }
        }
    }

    fun savePinAndComplete(pin: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.savePin(pin)
                _settingsState.value = SettingsState.Success("Registro completado")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al guardar el PIN")
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                stockRepository.deleteAllData()
                preferencesRepository.clearAllPreferences()
                _settingsState.value = SettingsState.Success("Cuenta eliminada")
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al eliminar la cuenta")
            }
        }
    }
}

sealed class SettingsState {
    object Idle : SettingsState()
    data class Success(val message: String) : SettingsState()
    data class Error(val message: String) : SettingsState()
}