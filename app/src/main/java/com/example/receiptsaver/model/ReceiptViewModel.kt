package com.example.receiptsaver.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receiptsaver.ui.ReceiptSaverApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ReceiptViewModel(private val application: ReceiptSaverApplication) : ViewModel() {
    private val receiptDao = ReceiptDatabase.getDatabase(application).receiptDao()
    private val _allReceipts = MutableStateFlow<List<Receipt>>(emptyList())

    val allReceipts: StateFlow<List<Receipt>> = _allReceipts

    init {
        viewModelScope.launch {
            receiptDao.getAllReceipts().collect { receipts ->
                _allReceipts.value = receipts
            }
        }
    }

    fun getReceiptById(id: String): Flow<Receipt?> {
        return receiptDao.getReceiptById(id)
    }

    fun saveReceipt(context: Context, store: String, date: String, amount: String, imageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { saveImageToInternalStorage(context, it) } ?: ""
            val receipt = Receipt(
                id = UUID.randomUUID().toString(),
                store = store,
                date = date,
                amount = amount,
                imagePath = imagePath
            )
            receiptDao.insertReceipt(receipt)
        }
    }

    fun updateReceipt(receiptId: String, store: String, date: String, amount: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val currentReceipt = receiptDao.getReceiptById(receiptId).first()
                currentReceipt?.let { receipt ->
                    // Keep the existing image path by default
                    var imagePath = receipt.imagePath
                    
                    // Only update image if a new one is explicitly provided
                    if (imageUri != null) {
                        try {
                            // Check if the new image is different from the current one
                            val isNewImage = try {
                                val currentFile = File(receipt.imagePath)
                                val newFile = File(imageUri.path ?: "")
                                !currentFile.exists() || !newFile.exists() || currentFile.absolutePath != newFile.absolutePath
                            } catch (e: Exception) {
                                true // If we can't compare, assume it's a new image
                            }

                            if (isNewImage) {
                                // Delete old image if it exists
                                if (receipt.imagePath.isNotEmpty()) {
                                    File(receipt.imagePath).delete()
                                }
                                imagePath = saveImageToInternalStorage(application, imageUri)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ReceiptViewModel", "Error updating image", e)
                            // Keep the old image path if there's an error
                            imagePath = receipt.imagePath
                        }
                    }

                    val updatedReceipt = receipt.copy(
                        store = store,
                        date = date,
                        amount = amount,
                        imagePath = imagePath
                    )
                    
                    // Update the receipt in the database
                    receiptDao.updateReceipt(updatedReceipt)
                    
                    // Update the local state
                    _allReceipts.value = _allReceipts.value.map { 
                        if (it.id == receiptId) updatedReceipt else it 
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ReceiptViewModel", "Error updating receipt", e)
                throw e // Re-throw the exception to be handled by the UI
            }
        }
    }

    fun deleteReceipt(receiptId: String) {
        viewModelScope.launch {
            try {
                val receipt = receiptDao.getReceiptById(receiptId).first()
                receipt?.let {
                    // Delete the image file if it exists
                    if (it.imagePath.isNotEmpty()) {
                        File(it.imagePath).delete()
                    }
                    receiptDao.deleteReceipt(it)
                    
                    // Update the local state
                    _allReceipts.value = _allReceipts.value.filter { receipt -> receipt.id != receiptId }
                }
            } catch (e: Exception) {
                android.util.Log.e("ReceiptViewModel", "Error deleting receipt", e)
                throw e // Re-throw the exception to be handled by the UI
            }
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "receipt_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        
        return file.absolutePath
    }
}