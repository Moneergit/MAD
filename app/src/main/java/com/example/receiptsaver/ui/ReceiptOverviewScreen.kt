package com.example.receiptsaver.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.receiptsaver.model.Receipt
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptOverviewScreen(
    navController: NavController,
    viewModel: ReceiptViewModel = viewModel(
        factory = ReceiptViewModelFactory(LocalContext.current.applicationContext as ReceiptSaverApplication)
    )
) {
    val receipts by viewModel.allReceipts.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Receipt Overview") })
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(receipts) { receipt ->
                ReceiptItem(receipt = receipt, navController = navController)
                Spacer(Modifier.height(8.dp))
            }
        }
    }



}

@Composable
fun ReceiptItem(receipt: Receipt, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("receiptDetail/${receipt.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (receipt.imagePath.isNotEmpty() && File(receipt.imagePath).exists()) {
                Image(
                    painter = rememberAsyncImagePainter(receipt.imagePath),
                    contentDescription = "Receipt image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
            }

            Column {
                Text(
                    text = receipt.store,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Date: ${receipt.date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Amount: ${receipt.amount}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
