package com.example.receiptsaver.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.receiptsaver.ui.navigation.NavigationBottomBar

@Composable
fun AppScaffold(
    navController: NavController,
    currentRoute: String,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBottomBar(navController = navController, currentRoute = currentRoute)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}