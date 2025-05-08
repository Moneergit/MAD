package com.example.receiptsaver

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.receiptsaver.model.auth

@Composable
fun EmptyScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Velkommen! Dashboard kommer snart...",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("empty") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log ud")
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun EmptyScreenPreview() {
    EmptyScreen(navController = rememberNavController())
}