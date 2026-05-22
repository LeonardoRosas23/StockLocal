package com.example.stocklocal.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val businessName by viewModel.businessName.collectAsState("")
    val currency by viewModel.currency.collectAsState("")
    val settingsState by viewModel.settingsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var newPin by remember { mutableStateOf("") }
    var newBusinessName by remember { mutableStateOf("") }
    var newCurrency by remember { mutableStateOf("") }

    LaunchedEffect(businessName) { if (newBusinessName.isEmpty()) newBusinessName = businessName }
    LaunchedEffect(currency) { if (newCurrency.isEmpty()) newCurrency = currency }

    LaunchedEffect(settingsState) {
        when (settingsState) {
            is SettingsState.Success -> {
                val message = (settingsState as SettingsState.Success).message
                if (message == "Cuenta eliminada") {
                    onAccountDeleted()
                } else {
                    snackbarHostState.showSnackbar(message)
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Borrar cuenta?") },
            text = {
                Text("Esta acción eliminará todos los productos, movimientos y configuraciones del negocio. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Sí, borrar todo") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Nombre del negocio", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newBusinessName,
                onValueChange = { newBusinessName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = { viewModel.saveBusinessName(newBusinessName) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar nombre") }

            HorizontalDivider()

            Text("Moneda", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newCurrency,
                onValueChange = { newCurrency = it },
                label = { Text("Moneda (ej. MXN, USD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = { viewModel.saveCurrency(newCurrency) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar moneda") }

            HorizontalDivider()

            Text("Cambiar PIN", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newPin,
                onValueChange = { if (it.length <= 4) newPin = it },
                label = { Text("Nuevo PIN (4 dígitos)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = { viewModel.savePin(newPin) },
                modifier = Modifier.fillMaxWidth(),
                enabled = newPin.length == 4
            ) { Text("Guardar PIN") }

            HorizontalDivider()

            Text("Zona de peligro", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Borrar cuenta") }
        }
    }
}