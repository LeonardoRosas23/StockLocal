package com.example.stocklocal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocklocal.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun validatePin(enteredPin: String) {
        viewModelScope.launch {
            val savedPin = preferencesRepository.userPin.first()
            _loginState.value = if (savedPin.isEmpty()) {
                LoginState.NoPinSet
            } else if (enteredPin == savedPin) {
                LoginState.Success
            } else {
                LoginState.Error("PIN incorrecto")
            }
        }
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