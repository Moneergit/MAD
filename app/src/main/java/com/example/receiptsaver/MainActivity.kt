package com.example.receiptsaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receiptsaver.ui.AddReceiptScreen
import com.example.receiptsaver.ui.ReceiptOverviewScreen
import com.example.receiptsaver.ui.theme.ReceiptSaverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReceiptSaverTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "add_receipt") {
        composable("add_receipt") {
            AddReceiptScreen(context = navController.context, navController = navController)
        }
        composable("overview") {
            ReceiptOverviewScreen(navController = navController)
        }
    }
}