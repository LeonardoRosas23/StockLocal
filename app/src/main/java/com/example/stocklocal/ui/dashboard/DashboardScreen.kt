package com.example.stocklocal.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToInventory: () -> Unit,
    onNavigateToLowStock: () -> Unit,
    onNavigateToMovements: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onExit: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val businessName by viewModel.businessName.collectAsState("")
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()
    val recentMovements by viewModel.recentMovements.collectAsState()
    val allProducts by viewModel.products.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(businessName) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.Close, contentDescription = "Salir")
                    }
                }
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Productos",
                        value = allProducts.size.toString(),
                        onClick = onNavigateToInventory
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Stock bajo",
                        value = lowStockProducts.size.toString(),
                        onClick = onNavigateToLowStock
                    )
                }
            }

            item {
                Text("Últimos movimientos", style = MaterialTheme.typography.titleMedium)
            }

            if (recentMovements.isEmpty()) {
                item {
                    Text(
                        "No hay movimientos registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(recentMovements) { movement ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(movement.productName, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${movement.type}: ${movement.quantity} unidades",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onNavigateToMovements,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar movimiento")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Card(modifier = modifier, onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}