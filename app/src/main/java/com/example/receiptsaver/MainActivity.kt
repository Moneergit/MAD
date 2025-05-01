package com.example.receiptsaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
            ReceiptSaverApp()
        }
    }
}

@Composable
fun ReceiptSaverApp() {
    ReceiptSaverTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ReceiptSaverAppPreview() {
    ReceiptSaverTheme {
        ReceiptSaverApp()
    }
}