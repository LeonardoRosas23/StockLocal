package com.example.stocklocal.ui.movements

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stocklocal.data.local.entity.Movement
import com.example.stocklocal.data.local.entity.Product
import com.example.stocklocal.ui.inventory.InventoryUiState
import com.example.stocklocal.ui.inventory.InventoryViewModel
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementScreen(
    onNavigateBack: () -> Unit,
    viewModel: MovementViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val inventoryState by inventoryViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var localMessage by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState()
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedDateLabel = selectedDateMillis?.let {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = it
        val localCal = Calendar.getInstance()
        localCal.set(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0
        )
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(localCal.time)
    }

    val allMovementsForExport = when (uiState) {
        is MovementUiState.Success -> (uiState as MovementUiState.Success).movements
        else -> emptyList()
    }

    val filteredMovementsForExport = filterMovementsByDate(
        movements = allMovementsForExport,
        selectedDateMillis = selectedDateMillis
    )

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            val exported = exportMovementsToCsv(
                context = context,
                uri = uri,
                movements = filteredMovementsForExport
            )

            localMessage = if (exported) {
                "Movimientos exportados correctamente"
            } else {
                "No se pudieron exportar los movimientos"
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(localMessage) {
        localMessage?.let {
            snackbarHostState.showSnackbar(it)
            localMessage = null
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedDateLabel != null)
                            "Movimientos: $selectedDateLabel"
                        else
                            "Movimientos"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    TextButton(
                        enabled = filteredMovementsForExport.isNotEmpty(),
                        onClick = {
                            val fileName = if (selectedDateLabel != null) {
                                "movimientos_${selectedDateLabel.replace("/", "-")}.csv"
                            } else {
                                "movimientos_${currentDateForFileName()}.csv"
                            }

                            exportLauncher.launch(fileName)
                        }
                    ) {
                        Text("Exportar")
                    }

                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Filtrar por fecha")
                    }

                    if (selectedDateMillis != null) {
                        TextButton(onClick = { selectedDateMillis = null }) {
                            Text("Ver todos")
                        }
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
                val allMovements = (uiState as MovementUiState.Success).movements
                val filteredMovements = filterMovementsByDate(
                    movements = allMovements,
                    selectedDateMillis = selectedDateMillis
                )

                if (filteredMovements.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (selectedDateMillis != null)
                                "No hay movimientos para esta fecha"
                            else
                                "No hay movimientos registrados"
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredMovements) { movement ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        movement.productName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
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
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is MovementUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se pudieron cargar los movimientos",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDialog) {
        val products = if (inventoryState is InventoryUiState.Success)
            (inventoryState as InventoryUiState.Success).products
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
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
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
                    onValueChange = {
                        if (it.isEmpty() || it.all { c -> c.isDigit() }) quantity = it
                    },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = quantity.isNotBlank() && (quantity.toIntOrNull() ?: 0) <= 0
                )

                if (quantity.isBlank()) {
                    Text(
                        text = "La cantidad es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedProduct?.let {
                        onConfirm(it, movementType, quantity.toIntOrNull() ?: 0)
                    }
                },
                enabled = selectedProduct != null &&
                        quantity.isNotEmpty() &&
                        (quantity.toIntOrNull() ?: 0) > 0
            ) { Text("Registrar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun filterMovementsByDate(
    movements: List<Movement>,
    selectedDateMillis: Long?
): List<Movement> {
    if (selectedDateMillis == null) return movements

    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.timeInMillis = selectedDateMillis

    val localCal = Calendar.getInstance()
    localCal.set(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0
    )

    val selectedDay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(localCal.time)

    return movements.filter { movement ->
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(movement.date)) == selectedDay
    }
}

private fun exportMovementsToCsv(
    context: Context,
    uri: Uri,
    movements: List<Movement>
): Boolean {
    return try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                writer.appendLine("Producto,Tipo,Cantidad,Fecha")

                movements.forEach { movement ->
                    writer.appendLine(
                        listOf(
                            movement.productName,
                            movement.type,
                            movement.quantity.toString(),
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                .format(Date(movement.date))
                        ).joinToString(",") { value -> value.toCsvValue() }
                    )
                }
            }
        } != null
    } catch (e: Exception) {
        false
    }
}

private fun String.toCsvValue(): String {
    val escaped = replace("\"", "\"\"")
    return "\"$escaped\""
}

private fun currentDateForFileName(): String {
    val formatter = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
    return formatter.format(Date())
}