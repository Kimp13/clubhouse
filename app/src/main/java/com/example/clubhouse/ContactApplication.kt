package com.example.clubhouse

import android.app.Application
import timber.log.Timber

import timber.log.Timber.DebugTree


class ContactApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(DebugTree())
    }
}