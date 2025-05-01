package com.example.receiptsaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.receiptsaver.ui.AddReceiptScreen
import com.example.receiptsaver.ui.theme.ReceiptSaverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReceiptSaverTheme {
                // Pass both context and navController to AddReceiptScreen
                AddReceiptScreen(context = this@MainActivity, navController = rememberNavController())
            }
        }
    }
}