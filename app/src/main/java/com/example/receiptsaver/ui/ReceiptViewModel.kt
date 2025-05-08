package com.example.receiptsaver.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receiptsaver.ReceiptSaverApplication
import com.example.receiptsaver.model.Receipt
import com.example.receiptsaver.model.ReceiptDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ReceiptViewModel(private val receiptDao: ReceiptDao) : ViewModel() {
    val allReceipts: Flow<List<Receipt>> = receiptDao.getAllReceipts()

    fun saveReceipt(
        context: Context,
        store: String,
        date: String,
        amount: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { uri ->
                saveImageToInternalStorage(context, uri)
            } ?: ""
            val receipt = Receipt(
                store = store,
                date = date,
                amount = amount,
                imagePath = imagePath
            )
            receiptDao.insert(receipt)
            android.util.Log.d("ReceiptViewModel", "Saved receipt: $receipt")
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
            val fileName = "receipt_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            return file.absolutePath
        } catch (e: Exception) {
            android.util.Log.e("ReceiptViewModel", "Error saving image", e)
            return ""
        }
    }
}