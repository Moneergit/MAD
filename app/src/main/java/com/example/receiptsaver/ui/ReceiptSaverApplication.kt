package com.example.receiptsaver.ui

import android.app.Application
import androidx.room.Room
import com.example.receiptsaver.model.AppDatabase

class ReceiptSaverApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "receipt_saver_database"
        ).build()
    }
}