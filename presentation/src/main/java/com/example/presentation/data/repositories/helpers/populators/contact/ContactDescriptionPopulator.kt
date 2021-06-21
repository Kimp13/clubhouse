package com.example.presentation.data.repositories.helpers.populators.contact

import android.database.Cursor
import androidx.core.database.getStringOrNull
import com.example.domain.entities.ContactEntity
import com.example.presentation.data.repositories.helpers.populators.DATA_FIELD_INDEX

class ContactDescriptionPopulator : ContactPopulator {
    override fun populate(contactEntity: ContactEntity, cursor: Cursor) =
        cursor.getStringOrNull(DATA_FIELD_INDEX)
            ?.let {
                contactEntity.copy(description = it)
            }
            ?: contactEntity
}
