package com.example.receiptsaver.ui

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
import com.example.receiptsaver.ReceiptSaverApplication
import com.example.receiptsaver.ui.navigation.NavigationBottomBar
import com.mindee.MindeeClient
import com.mindee.input.LocalInputSource
import com.mindee.product.receipt.ReceiptV5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReceiptScreen(
    context: Context,
    navController: NavController,
    viewModel: ReceiptViewModel = viewModel(
        factory = ReceiptViewModelFactory(context.applicationContext as ReceiptSaverApplication)
    ),
    onSaveReceipt: (NavController) -> Unit // Callback for navigation after save
) {
    val mindeeClient = MindeeClient("3cfee601cda8ab9190086b7f2f4ad93e")
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var store by remember { mutableStateOf("") }
    var datestate by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        android.util.Log.d("ImagePicker", "Selected URI: $uri")
    }

    // Date formatting utility
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.getDefault())

    // Use LaunchedEffect for image processing
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            android.util.Log.d("LaunchedEffect", "Processing image URI: $uri")
            val file = uriToFile(context, uri)
            file?.let {
                withContext(Dispatchers.IO) {
                    try {
                        val inputSource = LocalInputSource(file)
                        val response = mindeeClient.parse(ReceiptV5::class.java, inputSource)
                        val receipt = response.document.inference

                        // Log for debugging
                        android.util.Log.d("ReceiptDebug", "Full Receipt: ${receipt}")
                        android.util.Log.d("ReceiptDebug", "Prediction: ${receipt.prediction}")

                        // Extract fields
                        val merchant = receipt.prediction.supplierName?.value
                        val total = receipt.prediction.totalAmount?.value
                        android.util.Log.d("ReceiptDebug", "Merchant: $merchant, Total: $total")

                        // Update UI
                        withContext(Dispatchers.Main) {
                            store = merchant ?: "Unknown"
                            amount = total?.toString() ?: "Unknown"
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ReceiptDebug", "Error parsing receipt", e)
                        withContext(Dispatchers.Main) {
                            store = "Error parsing receipt"
                            amount = "Unknown"
                        }
                    } finally {
                        file.delete() // Clean up the temp file
                    }
                }
            } ?: android.util.Log.e("LaunchedEffect", "Failed to create file from URI")
        }
    }

    // UI with Scaffold
    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Add Receipt") })
        },
        bottomBar = {
            NavigationBottomBar(navController = navController, currentRoute = "add_receipt")
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image placeholder box with gallery picker
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        android.util.Log.d("ImagePicker", "Launching image picker")
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected receipt",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Tap to add receipt")
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

            // Date field with DatePicker
            OutlinedTextField(
                value = datestate,
                onValueChange = { /* Read-only, updated via DatePicker */ },
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        android.util.Log.d("DatePicker", "Opening DatePicker")
                        showDatePicker = true
                    },
                enabled = false, // Make it read-only
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date",
                        modifier = Modifier.clickable {
                            android.util.Log.d("DatePicker", "Opening DatePicker via icon")
                            showDatePicker = true
                        }
                    )
                }
            )

            // DatePickerDialog
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
                    onDismissRequest = {
                        android.util.Log.d("DatePicker", "DatePicker dismissed")
                        showDatePicker = false
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                datestate = date.format(dateFormatter)
                                android.util.Log.d("DatePicker", "Date selected: $datestate")
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            android.util.Log.d("DatePicker", "DatePicker cancelled")
                            showDatePicker = false
                        }) {
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

            Button(
                onClick = {
                    if (store.isNotBlank() && datestate.isNotBlank() && amount.isNotBlank()) {
                        viewModel.saveReceipt(context, store, datestate, amount, selectedImageUri)
                        onSaveReceipt(navController) // Delegate navigation to callback
                        android.util.Log.d("SaveReceipt", "Saved: Store=$store, Date=$datestate, Amount=$amount")
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill all required fields")
                        }
                        android.util.Log.w("SaveReceipt", "Missing required fields")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("receipt_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        tempFile
    } catch (e: Exception) {
        android.util.Log.e("UriToFile", "Error creating file from URI", e)
        null
    }
}