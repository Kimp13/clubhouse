package com.example.presentation.ui.interfaces

interface PermissionGateway {
    fun checkLocationPermission(): Boolean

    fun requestLocationPermission(handler: (Boolean) -> Unit)

    fun checkContactPermission(): Boolean

    fun requestContactPermission(onSuccess: () -> Unit)

    fun onRequestDialogDismissed()
}
