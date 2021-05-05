package com.example.presentation.interfaces

interface ReadContactsPermissionRequester {
    fun checkPermission(): Boolean
    fun requestPermission(onSuccess: () -> Unit)
}