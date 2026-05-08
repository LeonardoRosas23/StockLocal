package com.example.stocklocal.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stocklocal.data.local.entity.Product
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val suggestedCategory by viewModel.suggestedCategory.collectAsState()
    val currency by viewModel.currency.collectAsState("")
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is InventoryUiState.Error) {
            snackbarHostState.showSnackbar((uiState as InventoryUiState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is InventoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is InventoryUiState.Success -> {
                    val products = (uiState as InventoryUiState.Success).products
                        .filter { it.name.contains(searchQuery, ignoreCase = true) }

                    if (products.isEmpty()) {
                        Text("No hay productos registrados")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(products) { product ->
                                ProductCard(
                                    product = product,
                                    onDelete = { viewModel.deleteProduct(product) },
                                    currency = currency
                                )
                            }
                        }
                    }
                }
                is InventoryUiState.Error -> {}
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            suggestedCategory = suggestedCategory,
            onSearchCategory = { viewModel.searchCategoryOnline(it) },
            onDismiss = {
                showAddDialog = false
                viewModel.clearSuggestedCategory()
            },
            onConfirm = { product ->
                viewModel.insertProduct(product)
                showAddDialog = false
                viewModel.clearSuggestedCategory()
            }
        )
    }
}

@Composable
fun ProductCard(product: Product, onDelete: () -> Unit, currency: String = "MXN") {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.bodyLarge)
                Text(product.category, style = MaterialTheme.typography.bodySmall)
                Text(
                    "Cantidad: ${product.quantity}",
                    color = if (product.quantity <= 5)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Precio: $currency ${product.price}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
fun AddProductDialog(
    suggestedCategory: String?,
    onSearchCategory: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    LaunchedEffect(suggestedCategory) {
        if (suggestedCategory != null) category = suggestedCategory
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = { if (name.isNotEmpty()) onSearchCategory(name) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) { Text("Auto") }
                }
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Product(
                            name = name,
                            category = category,
                            quantity = quantity.toIntOrNull() ?: 0,
                            price = price.toDoubleOrNull() ?: 0.0
                        )
                    )
                },
                enabled = name.isNotEmpty()
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}