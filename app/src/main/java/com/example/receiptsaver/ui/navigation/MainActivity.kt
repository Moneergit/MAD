package com.example.receiptsaver.ui.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receiptsaver.ui.screens.LoginScreen
import com.example.receiptsaver.ui.screens.SignUpScreen
import com.example.receiptsaver.ui.AddReceiptScreen
import com.example.receiptsaver.ui.screens.ReceiptOverviewScreen
import com.example.receiptsaver.ui.AppScaffold
import com.example.receiptsaver.ui.screens.HomeScreen
import com.example.receiptsaver.ui.screens.ProfileScreen
import com.example.receiptsaver.ui.screens.EditReceiptScreen
import com.example.receiptsaver.ui.theme.ReceiptSaverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReceiptSaverTheme {
                ReceiptSaverApp()
            }
        }
    }
}

@Composable
fun ReceiptSaverApp() {
    val navController = rememberNavController()
    val startDestination = "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("add_receipt") {
            AppScaffold(navController = navController, currentRoute = "add_receipt") {
                AddReceiptScreen(
                    context = navController.context,
                    navController = navController,
                    onSaveReceipt = { navigateToOverview(it) }
                )
            }
        }
        composable("home") {
            AppScaffold(navController = navController, currentRoute = "home") {
                HomeScreen(navController = navController)
            }
        }
        composable("profile") {
            AppScaffold(navController = navController, currentRoute = "profile") {
                ProfileScreen(navController = navController)
            }
        }
        composable("overview") {
            AppScaffold(navController = navController, currentRoute = "overview") {
                ReceiptOverviewScreen(navController = navController)
            }
        }
        composable("edit_receipt/{receiptId}") { backStackEntry ->
            AppScaffold(navController = navController, currentRoute = "edit_receipt") {
                EditReceiptScreen(
                    context = navController.context,
                    navController = navController,
                    receiptId = backStackEntry.arguments?.getString("receiptId") ?: ""
                )
            }
        }
    }
}