package com.example.stocklocal.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stocklocal.ui.settings.SettingsViewModel
import com.example.stocklocal.ui.settings.SettingsState
import com.example.stocklocal.data.repository.PreferencesRepository
import androidx.compose.ui.text.style.TextAlign

@Composable
fun RegisterScreen(
    onRegistrationComplete: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    hasExistingAccount: Boolean = false,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var businessName by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("MXN") }
    var pin by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val settingsState by viewModel.settingsState.collectAsState()

    LaunchedEffect(settingsState) {
        when (settingsState) {
            is SettingsState.Success -> {
                val message = (settingsState as SettingsState.Success).message
                if (message == "Registro completado") {
                    onRegistrationComplete()
                }
                viewModel.resetState()
            }
            is SettingsState.Error -> {
                snackbarHostState.showSnackbar((settingsState as SettingsState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "📦", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido a StockLocal",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (hasExistingAccount) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Ya existe una cuenta registrada en este dispositivo. Para crear una nueva debes borrar la cuenta actual desde Ajustes.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (onNavigateBack != null) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Regresar al Login")
                    }
                }
            } else {
                Text(
                    text = "Configura tu negocio para comenzar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Nombre del negocio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Moneda (ej. MXN, USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text("PIN de acceso (4 dígitos)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pinConfirm,
                    onValueChange = { if (it.length <= 4) pinConfirm = it },
                    label = { Text("Confirmar PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.saveBusinessName(businessName)
                        viewModel.saveCurrency(currency)
                        viewModel.savePinAndComplete(pin)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = businessName.isNotEmpty() && pin.length == 4 && pin == pinConfirm
                ) {
                    Text("Registrar negocio")
                }
            }
        }
    }
}