package com.example.clubhouse.ui.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.clubhouse.ContactApplication
import com.example.clubhouse.data.repositories.ContactRepository
import com.example.clubhouse.di.scopes.ServiceScope
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@Subcomponent
@ServiceScope
interface StartedContactServiceComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): StartedContactServiceComponent
    }

    fun inject(service: StartedContactService)
}

abstract class StartedContactService : Service(),
    CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    @Inject
    lateinit var repository: ContactRepository

    private val job = Job()

    @SuppressLint("WrongConstant")
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        (application as? ContactApplication)?.applicationComponent
            ?.startedContactServiceComponent()
            ?.create()
            ?.inject(this)

        return 0
    }

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