package com.example.receiptsaver.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.receiptsaver.model.ReceiptViewModel

class ReceiptViewModelFactory(private val application: ReceiptSaverApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReceiptViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}