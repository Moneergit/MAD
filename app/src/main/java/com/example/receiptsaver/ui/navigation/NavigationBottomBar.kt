package com.example.receiptsaver.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.receiptsaver.R

@Composable
fun NavigationBottomBar(navController: NavController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = currentRoute == "home",
            onClick = { navigateToHome(navController) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_receipt_long_24),
                    contentDescription = "Receipts"
                )
            },
            selected = currentRoute == "add_receipt",
            onClick = { navigateToAddReceipt(navController) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.overview),
                    contentDescription = "Overview"
                )
            },
            selected = currentRoute == "overview",
            onClick = { navigateToOverview(navController) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = currentRoute == "profile",
            onClick = { navigateToProfile(navController) }
        )

    }
}

fun navigateToAddReceipt(navController: NavController){
    navController.navigate("add_receipt"){
        popUpTo(navController.graph.startDestinationId) { inclusive = false }
        launchSingleTop = true
    }
}

fun navigateToHome(navController: NavController) {
    navController.navigate("home") {
        popUpTo(navController.graph.startDestinationId) { inclusive = false }
        launchSingleTop = true
    }
}

fun navigateToProfile(navController: NavController) {
    navController.navigate("profile") {
        popUpTo(navController.graph.startDestinationId) { inclusive = false }
        launchSingleTop = true
    }
}

fun navigateToOverview(navController: NavController) {
    navController.navigate("overview") {
        popUpTo(navController.graph.startDestinationId) { inclusive = false }
        launchSingleTop = true
    }
}