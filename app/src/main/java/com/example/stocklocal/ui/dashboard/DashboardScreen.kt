package com.example.stocklocal.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToMovements) {
                Icon(Icons.Default.Add, contentDescription = "Registrar movimiento")
            }
        }
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
                        onClick = onNavigateToLowStock,
                        containerColor = if (lowStockProducts.isNotEmpty())
                            Color(0xFFF4B300)
                        else
                            MaterialTheme.colorScheme.surface
                    )
                }
            }

            item {
                Text(
                    text = "Últimos movimientos",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (recentMovements.isEmpty()) {
                item {
                    Text(
                        text = "No hay movimientos registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(recentMovements) { movement ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                movement.productName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "${movement.type}: ${movement.quantity} unidades",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (movement.type == "Entrada")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                            Text(
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    .format(Date(movement.date)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}