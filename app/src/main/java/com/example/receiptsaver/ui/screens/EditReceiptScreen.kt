package com.example.receiptsaver.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.receiptsaver.ui.ReceiptSaverApplication
import com.example.receiptsaver.model.ReceiptViewModel
import com.example.receiptsaver.ui.ReceiptViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptScreen(
    context: Context,
    navController: NavController,
    receiptId: String,
    viewModel: ReceiptViewModel = viewModel(
        factory = ReceiptViewModelFactory(context.applicationContext as ReceiptSaverApplication)
    )
) {
    val receipt by viewModel.getReceiptById(receiptId).collectAsState(initial = null)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var store by remember { mutableStateOf("") }
    var datestate by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(receipt) {
        receipt?.let {
            store = it.store
            datestate = it.date
            amount = it.amount
            if (it.imagePath.isNotEmpty()) {
                try {
                    val file = File(it.imagePath)
                    if (file.exists()) {
                        selectedImageUri = Uri.fromFile(file)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("EditReceiptScreen", "Error loading image", e)
                }
            }
            isLoading = false
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Edit Receipt") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Receipt image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Tap to change image")
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = store,
                    onValueChange = { store = it },
                    label = { Text("Store") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = datestate,
                    onValueChange = { },
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date",
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    }
                )

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = datestate.takeIf { it.isNotEmpty() }?.let {
                            try {
                                LocalDate.parse(it, dateFormatter).toEpochDay() * 24 * 60 * 60 * 1000
                            } catch (e: Exception) {
                                System.currentTimeMillis()
                            }
                        } ?: System.currentTimeMillis()
                    )

                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                    datestate = date.format(dateFormatter)
                                }
                                showDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (store.isNotBlank() && datestate.isNotBlank() && amount.isNotBlank()) {
                                try {
                                    viewModel.updateReceipt(
                                        receiptId = receiptId,
                                        store = store,
                                        date = datestate,
                                        amount = amount,
                                        imageUri = selectedImageUri
                                    )
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error saving changes: ${e.message}")
                                    }
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fill all required fields")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Changes")
                    }

                    Button(
                        onClick = {
                            try {
                                viewModel.deleteReceipt(receiptId)
                                navController.popBackStack()
                            } catch (e: Exception) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error deleting receipt: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
} 