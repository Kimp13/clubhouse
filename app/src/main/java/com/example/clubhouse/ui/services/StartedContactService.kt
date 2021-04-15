package com.example.clubhouse.ui.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.content.ContextCompat
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class StartedContactService : Service() {
    protected val disposable = CompositeDisposable()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        disposable.dispose()

        super.onDestroy()
    }

    fun checkReadContactsPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
}