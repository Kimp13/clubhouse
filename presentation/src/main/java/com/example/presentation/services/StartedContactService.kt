package com.example.presentation.services

import android.Manifest
import android.app.Service
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface ServiceComponent {
    interface Factory {
        fun create(): ServiceComponent
    }

    fun inject(service: BirthdayNotificationService)
    fun inject(service: RebootReminderService)
}

abstract class StartedContactService : Service(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val job = Job()

    override fun onDestroy() {
        job.cancel()

        super.onDestroy()
    }

    fun checkReadContactsPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
}