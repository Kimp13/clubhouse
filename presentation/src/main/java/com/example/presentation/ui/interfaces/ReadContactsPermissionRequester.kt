package com.example.presentation.ui.interfaces

interface ReadContactsPermissionRequester {
    fun checkContactPermission(): Boolean
    fun requestContactPermission(onSuccess: () -> Unit)
}
