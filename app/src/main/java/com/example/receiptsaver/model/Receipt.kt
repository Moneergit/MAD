package com.example.receiptsaver.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val store: String,
    val date: String, // Format: dd-MMMM-yyyy
    val amount: String,
    val imagePath: String // Path to the saved image file
)
