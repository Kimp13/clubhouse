package com.example.presentation.data.repositories.helpers.populators.contact

import android.database.Cursor
import androidx.core.database.getLongOrNull
import com.example.domain.entities.ContactEntity
import com.example.presentation.data.repositories.helpers.populators.DATA_PHOTO_ID_INDEX

class ContactPhotoPopulator : ContactPopulator {
    override fun populate(contactEntity: ContactEntity, cursor: Cursor) =
        cursor.getLongOrNull(DATA_PHOTO_ID_INDEX)
            ?.takeIf { it > 0 }
            ?.let {
                contactEntity.copy(photoId = it)
            }
            ?: contactEntity
}
