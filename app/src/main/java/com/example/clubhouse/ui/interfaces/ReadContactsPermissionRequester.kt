package com.example.clubhouse.ui.interfaces

interface ReadContactsPermissionRequester {
    fun checkContactPermission(): Boolean
    fun requestContactPermission(onSuccess: () -> Unit)
}