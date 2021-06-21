package com.example.presentation.data.repositories.helpers.populators.simplecontact

import android.database.Cursor
import androidx.core.database.getLongOrNull
import com.example.domain.entities.SimpleContactEntity
import com.example.presentation.data.repositories.helpers.populators.DATA_PHOTO_ID_INDEX

class SimpleContactPhotoPopulator : SimpleContactPopulator {
    override fun populate(
        simpleContactEntity: SimpleContactEntity,
        cursor: Cursor
    ) = cursor.getLongOrNull(DATA_PHOTO_ID_INDEX)
        ?.takeIf { it > 0 }
        ?.let { photoId ->
            simpleContactEntity.copy(photoId = photoId)
        }
        ?: simpleContactEntity
}
