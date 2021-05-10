package com.example.presentation.ui.interfaces

interface AccessLocationPermissionRequester {
    fun checkLocationPermission(): Boolean
    fun requestLocationPermission(handler: (Boolean) -> Unit)
}