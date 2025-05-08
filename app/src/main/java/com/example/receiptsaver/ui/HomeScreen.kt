package com.example.receiptsaver.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.receiptsaver.model.Receipt
import com.example.receiptsaver.ReceiptSaverApplication
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ReceiptViewModel = viewModel(
        factory = ReceiptViewModelFactory(LocalContext.current.applicationContext as ReceiptSaverApplication)
    )
) {
    val receipts by viewModel.allReceipts.collectAsState(initial = emptyList())

    val latestThreeReceipts = receipts.takeLast(3).reversed()
    val lastMonth = YearMonth.now().minusMonths(1)
    val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.getDefault())

    val lastMonthReceipts = receipts.filter {
        try {
            val date = LocalDate.parse(it.date, formatter)
            YearMonth.from(date) == lastMonth
        } catch (e: Exception) {
            false
        }
    }

    val totalLastMonth = lastMonthReceipts.sumOf {
        it.amount.toDoubleOrNull() ?: 0.0
    }

    val totalReceipts = receipts.size

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Dashboard") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Total receipts: $totalReceipts", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Total spent last month: ${"%.2f".format(totalLastMonth)} DKK", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            Text("Latest 3 receipts:", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(latestThreeReceipts) { receipt ->
                    ReceiptItem(receipt = receipt, navController = navController)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
