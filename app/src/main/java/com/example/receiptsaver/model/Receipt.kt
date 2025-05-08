package com.example.receiptsaver.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey
    val id: String,
    val store: String,
    val date: String, // Format: dd-MMMM-yyyy
    val amount: String,
    val imagePath: String
)
