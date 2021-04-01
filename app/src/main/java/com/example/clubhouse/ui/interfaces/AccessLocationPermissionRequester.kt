package com.example.clubhouse.ui.interfaces

interface AccessLocationPermissionRequester {
    fun checkLocationPermission(): Boolean
    fun requestLocationPermission(handler: (Boolean) -> Unit)
}