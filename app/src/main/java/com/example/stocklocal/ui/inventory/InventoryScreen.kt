package com.example.stocklocal.ui.inventory

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.stocklocal.data.local.entity.Product
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ALL_CATEGORIES = "Todas"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit,
    showLowStock: Boolean = false,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val suggestedCategory by viewModel.suggestedCategory.collectAsState()
    val categorySearchMessage by viewModel.categorySearchMessage.collectAsState()
    val message by viewModel.message.collectAsState()
    val currency by viewModel.currency.collectAsState("")
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ALL_CATEGORIES) }
    var localMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val allProducts = when (uiState) {
        is InventoryUiState.Success -> (uiState as InventoryUiState.Success).products
        else -> emptyList()
    }

    val lowStockProducts = allProducts.filter { it.quantity <= 15 }

    val availableCategories = listOf(ALL_CATEGORIES) + lowStockProducts
        .map { it.category.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()

    LaunchedEffect(availableCategories) {
        if (selectedCategory !in availableCategories) {
            selectedCategory = ALL_CATEGORIES
        }
    }

    val visibleProducts = allProducts
        .filter { if (showLowStock) it.quantity <= 15 else true }
        .filter {
            if (showLowStock && selectedCategory != ALL_CATEGORIES) {
                it.category.equals(selectedCategory, ignoreCase = true)
            } else {
                true
            }
        }
        .filter { it.name.contains(searchQuery, ignoreCase = true) }

    val productsForExport = visibleProducts

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            val exported = exportProductsToCsv(
                context = context,
                uri = uri,
                products = productsForExport,
                currency = currency
            )

            localMessage = if (exported) {
                "Inventario exportado correctamente"
            } else {
                "No se pudo exportar el inventario"
            }
        }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(localMessage) {
        localMessage?.let {
            snackbarHostState.showSnackbar(it)
            localMessage = null
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is InventoryUiState.Error) {
            snackbarHostState.showSnackbar((uiState as InventoryUiState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (showLowStock) "Stock bajo" else "Inventario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    TextButton(
                        enabled = productsForExport.isNotEmpty(),
                        onClick = {
                            val fileName = if (showLowStock) {
                                val categorySuffix = if (selectedCategory == ALL_CATEGORIES) {
                                    "todos"
                                } else {
                                    selectedCategory
                                        .lowercase()
                                        .replace(" ", "_")
                                        .replace("/", "_")
                                }

                                "stock_bajo_${categorySuffix}_${currentDateForFileName()}.csv"
                            } else {
                                "inventario_${currentDateForFileName()}.csv"
                            }

                            exportLauncher.launch(fileName)
                        }
                    ) {
                        Text("Exportar")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!showLowStock) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                }
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

            if (showLowStock) {
                Spacer(modifier = Modifier.height(12.dp))

                CategoryFilterDropdown(
                    categories = availableCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is InventoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is InventoryUiState.Success -> {
                    if (visibleProducts.isEmpty()) {
                        EmptyInventoryMessage(
                            showLowStock = showLowStock,
                            searchQuery = searchQuery,
                            hasProducts = allProducts.isNotEmpty(),
                            selectedCategory = selectedCategory
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(visibleProducts) { product ->
                                ProductCard(
                                    product = product,
                                    showDelete = !showLowStock,
                                    onDelete = { productToDelete = product },
                                    onClick = { selectedProduct = product },
                                    currency = currency
                                )
                            }
                        }
                    }
                }

                is InventoryUiState.Error -> {
                    Text(
                        text = "No se pudo cargar el inventario. Intenta nuevamente.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            suggestedCategory = suggestedCategory,
            categorySearchMessage = categorySearchMessage,
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

    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            currency = currency,
            canEdit = !showLowStock,
            onDismiss = { selectedProduct = null },
            onDelete = {
                selectedProduct = null
                productToDelete = product
            },
            onSave = { updatedProduct ->
                viewModel.updateProduct(updatedProduct)
                selectedProduct = null
            }
        )
    }

    productToDelete?.let { product ->
        DeleteProductConfirmationDialog(
            product = product,
            onDismiss = { productToDelete = null },
            onConfirm = {
                viewModel.deleteProduct(product)
                productToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterDropdown(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyInventoryMessage(
    showLowStock: Boolean,
    searchQuery: String,
    hasProducts: Boolean,
    selectedCategory: String = ALL_CATEGORIES
) {
    val message = when {
        searchQuery.isNotBlank() && selectedCategory != ALL_CATEGORIES ->
            "No se encontraron productos con ese nombre en la categoría seleccionada"

        searchQuery.isNotBlank() ->
            "No se encontraron productos con ese nombre"

        showLowStock && selectedCategory != ALL_CATEGORIES ->
            "No hay productos con stock bajo en esta categoría"

        showLowStock ->
            "No hay productos con stock bajo"

        !hasProducts ->
            "Aún no hay productos en el inventario. Agrega uno usando el botón +"

        else ->
            "No hay productos para mostrar"
    }

    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun ProductCard(
    product: Product,
    showDelete: Boolean = true,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    currency: String = "MXN"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
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
                    "Precio de referencia: $currency ${product.price}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (showDelete) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    suggestedCategory: String?,
    categorySearchMessage: String?,
    onSearchCategory: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    val isQuantityValid = quantity.toIntOrNull() != null && (quantity.toIntOrNull() ?: -1) >= 0
    val isPriceValid = price.toDoubleOrNull() != null && (price.toDoubleOrNull() ?: -1.0) >= 0.0

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
                    singleLine = true,
                    isError = name.isBlank()
                )
                if (name.isBlank()) {
                    Text(
                        text = "El nombre es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = category.isBlank()
                    )
                    Button(
                        onClick = { if (name.isNotBlank()) onSearchCategory(name) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) { Text("Auto") }
                }

                categorySearchMessage?.let {
                    Text(
                        text = it,
                        color = if (it.contains("aplicada", ignoreCase = true))
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (category.isBlank()) {
                    Text(
                        text = "La categoría es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) quantity = it },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = quantity.isNotBlank() && !isQuantityValid
                )
                if (quantity.isBlank()) {
                    Text(
                        text = "La cantidad es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) price = it
                    },
                    label = { Text("Precio de referencia") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = price.isNotBlank() && !isPriceValid
                )
                if (price.isBlank()) {
                    Text(
                        text = "El precio de referencia es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Product(
                            name = name.trim(),
                            category = category.trim(),
                            quantity = quantity.toIntOrNull() ?: 0,
                            price = price.toDoubleOrNull() ?: 0.0
                        )
                    )
                },
                enabled = name.isNotBlank() &&
                        category.isNotBlank() &&
                        isQuantityValid &&
                        isPriceValid
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun ProductDetailDialog(
    product: Product,
    currency: String,
    canEdit: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (Product) -> Unit
) {
    var isEditing by remember(product.id) { mutableStateOf(false) }
    var name by remember(product.id) { mutableStateOf(product.name) }
    var category by remember(product.id) { mutableStateOf(product.category) }
    var quantity by remember(product.id) { mutableStateOf(product.quantity.toString()) }
    var price by remember(product.id) { mutableStateOf(product.price.toString()) }

    val isQuantityValid = quantity.toIntOrNull() != null && (quantity.toIntOrNull() ?: -1) >= 0
    val isPriceValid = price.toDoubleOrNull() != null && (price.toDoubleOrNull() ?: -1.0) >= 0.0

    val isValid = name.isNotBlank() &&
            category.isNotBlank() &&
            isQuantityValid &&
            isPriceValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar producto" else "Detalle del producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isEditing) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        isError = name.isBlank()
                    )
                    if (name.isBlank()) {
                        Text(
                            text = "El nombre es obligatorio",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        singleLine = true,
                        isError = category.isBlank()
                    )
                    if (category.isBlank()) {
                        Text(
                            text = "La categoría es obligatoria",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = {
                            if (it.isEmpty() || it.all { c -> c.isDigit() }) quantity = it
                        },
                        label = { Text("Cantidad en stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = quantity.isNotBlank() && !isQuantityValid
                    )
                    if (quantity.isBlank()) {
                        Text(
                            text = "La cantidad es obligatoria",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedTextField(
                        value = price,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) price = it
                        },
                        label = { Text("Precio de referencia") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = price.isNotBlank() && !isPriceValid
                    )
                    if (price.isBlank()) {
                        Text(
                            text = "El precio de referencia es obligatorio",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Text("Nombre: ${product.name}")
                    Text("Categoría: ${product.category}")
                    Text("Cantidad en stock: ${product.quantity}")
                    Text("Precio de referencia: $currency ${product.price}")
                }
            }
        },
        confirmButton = {
            if (isEditing) {
                Button(
                    onClick = {
                        onSave(
                            product.copy(
                                name = name.trim(),
                                category = category.trim(),
                                quantity = quantity.toIntOrNull() ?: product.quantity,
                                price = price.toDoubleOrNull() ?: product.price
                            )
                        )
                    },
                    enabled = isValid
                ) {
                    Text("Guardar cambios")
                }
            } else if (canEdit) {
                Button(onClick = { isEditing = true }) {
                    Text("Editar producto")
                }
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (canEdit && !isEditing) {
                    TextButton(onClick = onDelete) {
                        Text("Eliminar")
                    }
                }

                TextButton(
                    onClick = {
                        if (isEditing) {
                            isEditing = false
                            name = product.name
                            category = product.category
                            quantity = product.quantity.toString()
                            price = product.price.toString()
                        } else {
                            onDismiss()
                        }
                    }
                ) {
                    Text(if (isEditing) "Cancelar" else "Cerrar")
                }
            }
        }
    )
}

@Composable
fun DeleteProductConfirmationDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar producto") },
        text = {
            Text(
                text = "¿Deseas eliminar \"${product.name}\"? Esta acción no se puede deshacer."
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun exportProductsToCsv(
    context: Context,
    uri: Uri,
    products: List<Product>,
    currency: String
): Boolean {
    return try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                writer.appendLine("Nombre,Categoria,Cantidad,Precio de referencia,Moneda")

                products.forEach { product ->
                    writer.appendLine(
                        listOf(
                            product.name,
                            product.category,
                            product.quantity.toString(),
                            product.price.toString(),
                            currency
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