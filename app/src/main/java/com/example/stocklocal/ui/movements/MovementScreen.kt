package com.example.stocklocal.ui.movements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stocklocal.data.local.entity.Product
import com.example.stocklocal.data.repository.StockRepository
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stocklocal.ui.inventory.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementScreen(
    onNavigateBack: () -> Unit,
    viewModel: MovementViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val inventoryState by inventoryViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is MovementUiState.Error) {
            snackbarHostState.showSnackbar((uiState as MovementUiState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Movimientos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        when (uiState) {
            is MovementUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MovementUiState.Success -> {
                val movements = (uiState as MovementUiState.Success).movements
                if (movements.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("No hay movimientos registrados")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(movements) { movement ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(movement.productName, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "${movement.type}: ${movement.quantity} unidades",
                                        color = if (movement.type == "Entrada")
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                            .format(Date(movement.date)),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is MovementUiState.Error -> {}
        }
    }

    if (showDialog) {
        val products = if (inventoryState is com.example.stocklocal.ui.inventory.InventoryUiState.Success)
            (inventoryState as com.example.stocklocal.ui.inventory.InventoryUiState.Success).products
        else emptyList()

        RegisterMovementDialog(
            products = products,
            onDismiss = { showDialog = false },
            onConfirm = { product, type, quantity ->
                viewModel.registerMovement(product, type, quantity)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMovementDialog(
    products: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (Product, String, Int) -> Unit
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var movementType by remember { mutableStateOf("Entrada") }
    var quantity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar movimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedProduct?.name ?: "Seleccionar producto",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Producto") },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        products.forEach { product ->
                            DropdownMenuItem(
                                text = { Text(product.name) },
                                onClick = {
                                    selectedProduct = product
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Entrada", "Salida").forEach { type ->
                        FilterChip(
                            selected = movementType == type,
                            onClick = { movementType = type },
                            label = { Text(type) }
                        )
                    }
                }
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedProduct?.let {
                        onConfirm(it, movementType, quantity.toIntOrNull() ?: 0)
                    }
                },
                enabled = selectedProduct != null && quantity.isNotEmpty()
            ) { Text("Registrar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}