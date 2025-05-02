package com.example.receiptsaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receiptsaver.ui.AddReceiptScreen
import com.example.receiptsaver.ui.ReceiptOverviewScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val startDestination = if (auth.currentUser != null) "empty" else "login"

    NavHost(navController = navController, startDestination = startDestination,) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("empty") { EmptyScreen(navController) }
        composable("add_receipt") {
            AddReceiptScreen(context = navController.context, navController = navController)
        }
        composable("receipt_overview") {
            ReceiptOverviewScreen(navController = navController)
        }
    }
}

//@Preview(showBackground = true, device = "id:pixel_5")
//@Composable
//fun ReceiptSaverAppPreview() {
//    ReceiptSaverTheme {
//        ReceiptSaverApp()
//    }
//}