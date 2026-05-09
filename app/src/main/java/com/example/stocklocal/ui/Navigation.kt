package com.example.stocklocal.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stocklocal.data.repository.PreferencesRepository
import com.example.stocklocal.ui.dashboard.DashboardScreen
import com.example.stocklocal.ui.inventory.InventoryScreen
import com.example.stocklocal.ui.login.LoginScreen
import com.example.stocklocal.ui.movements.MovementScreen
import com.example.stocklocal.ui.register.RegisterScreen
import com.example.stocklocal.ui.settings.SettingsScreen
import kotlinx.coroutines.flow.first

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val INVENTORY = "inventory"
    const val MOVEMENTS = "movements"
    const val SETTINGS = "settings"
}

@Composable
fun StockLocalNavigation(
    preferencesRepository: PreferencesRepository
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val pin = preferencesRepository.userPin.first()
        startDestination = if (pin.isEmpty()) {
            "${Routes.REGISTER}/false"
        } else {
            Routes.LOGIN
        }
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {
        composable(
            route = "${Routes.REGISTER}/{hasAccount}",
            arguments = listOf(
                navArgument("hasAccount") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val hasAccount = backStackEntry.arguments?.getBoolean("hasAccount") ?: false
            RegisterScreen(
                onRegistrationComplete = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo("${Routes.REGISTER}/$hasAccount") { inclusive = true }
                    }
                },
                onNavigateBack = if (hasAccount) {
                    { navController.popBackStack() }
                } else null,
                hasExistingAccount = hasAccount
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNoPinSet = {
                    navController.navigate("${Routes.REGISTER}/false") {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNewBusiness = {
                    navController.navigate("${Routes.REGISTER}/true")
                }
            )
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToInventory = {
                    navController.navigate("${Routes.INVENTORY}/false")
                },
                onNavigateToLowStock = {
                    navController.navigate("${Routes.INVENTORY}/true")
                },
                onNavigateToMovements = { navController.navigate(Routes.MOVEMENTS) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onExit = { android.os.Process.killProcess(android.os.Process.myPid()) }
            )
        }
        composable(
            route = "${Routes.INVENTORY}/{showLowStock}",
            arguments = listOf(
                navArgument("showLowStock") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val showLowStock = backStackEntry.arguments?.getBoolean("showLowStock") ?: false
            InventoryScreen(
                onNavigateBack = { navController.popBackStack() },
                showLowStock = showLowStock
            )
        }
        composable(Routes.MOVEMENTS) {
            MovementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onAccountDeleted = {
                    navController.navigate("${Routes.REGISTER}/false") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}