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
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        preferencesRepository.userPin.first()
        isLoading = false
    }

    if (isLoading) {
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
        startDestination = Routes.LOGIN
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
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo("${Routes.REGISTER}/$hasAccount") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                hasExistingAccount = hasAccount
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNoPinSet = {
                    // El LoginViewModel ya muestra un mensaje amigable.
                    // No navegamos automáticamente al registro para que el botón
                    // "Registrar negocio" conserve su propósito.
                },
                onNewBusiness = {
                    navController.navigate("${Routes.REGISTER}/false")
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
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}