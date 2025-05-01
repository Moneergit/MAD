package com.example.receiptsaver.model

data class LineItem(
    val description: String,
    val total: Double
)

data class Receipt(
    val id: String,
    val imageUri: String,
    val vendor: String,
    val total: Double,
    val date: String,
    val items: List<LineItem>
)
//in order to have the list of receipts
// val receiptList = remember { mutableStateListOf<Receipt>() }