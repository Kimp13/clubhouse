package com.example.clubhouse.ui.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.clubhouse.data.DataSource
import com.example.clubhouse.data.SimpleContactEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactService : Service() {
    private val binder = ContactBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    suspend fun getSimpleContacts():
            List<SimpleContactEntity> = withContext(Dispatchers.IO) {
        DataSource.getSimpleContacts(contentResolver)
    }

    suspend fun getContact(lookup: String) = withContext(Dispatchers.IO) {
        DataSource.getContact(contentResolver, lookup)
    }

    inner class ContactBinder : Binder() {
        fun getService() = this@ContactService
    }
}