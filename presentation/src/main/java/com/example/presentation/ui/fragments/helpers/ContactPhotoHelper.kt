package com.example.presentation.ui.fragments.helpers

import android.content.ContentUris
import android.provider.ContactsContract

class ContactPhotoHelper {
    fun makePhotoUri(photoId: Long) = ContentUris.withAppendedId(
        ContactsContract.DisplayPhoto.CONTENT_URI,
        photoId
    )
}
