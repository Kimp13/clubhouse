package com.example.presentation.ui.delegates

import android.content.ContentUris
import android.provider.ContactsContract

object ContactPhotoDelegate {
    fun makePhotoUri(photoId: Long) =
        ContentUris.withAppendedId(
            ContactsContract.DisplayPhoto.CONTENT_URI,
            photoId
        )
}
