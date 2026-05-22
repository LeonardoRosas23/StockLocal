package com.example.stocklocal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocklocal.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    val hasRegisteredBusiness: StateFlow<Boolean> = preferencesRepository.userPin
        .map { it.isNotBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun validatePin(enteredPin: String) {
        viewModelScope.launch {
            val savedPin = preferencesRepository.userPin.first()

            _loginState.value = when {
                savedPin.isEmpty() -> {
                    LoginState.Error("No hay un negocio registrado. Usa la opción \"Registrar negocio\".")
                }

                preferencesRepository.isPinValid(enteredPin, savedPin) -> {
                    LoginState.Success
                }

                else -> {
                    LoginState.Error("PIN incorrecto")
                }
            }
        }
    }

    fun showBusinessAlreadyRegisteredMessage() {
        _loginState.value = LoginState.Error(
            "Ya existe una cuenta registrada en este dispositivo."
        )
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Success : LoginState()
    object NoPinSet : LoginState()
    data class Error(val message: String) : LoginState()
}