package com.example.clubhouse.ui.interfaces

interface ReadContactsPermissionRequester {
    fun checkPermission(): Boolean
    fun requestPermission(onSuccess: () -> Unit)
}