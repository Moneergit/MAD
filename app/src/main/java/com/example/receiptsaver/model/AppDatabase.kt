package com.example.receiptsaver.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Receipt::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}