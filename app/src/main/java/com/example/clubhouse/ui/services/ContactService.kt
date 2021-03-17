package com.example.clubhouse.ui.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.clubhouse.data.MockDataSource
import kotlinx.coroutines.*

class ContactService : Service() {
    private val binder = ContactBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    suspend fun getContacts() = withContext(Dispatchers.IO) {
        delay(500)

        MockDataSource.getSimpleContacts()
    }

    suspend fun getContact(id: Int) = withContext(Dispatchers.IO) {
        delay(500)

        MockDataSource.getContact(id)
    }

    inner class ContactBinder : Binder() {
        fun getService() = this@ContactService
    }
}