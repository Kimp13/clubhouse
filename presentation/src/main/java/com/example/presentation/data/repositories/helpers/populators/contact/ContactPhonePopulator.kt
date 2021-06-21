package com.example.presentation.data.repositories.helpers.populators.contact

import android.database.Cursor
import androidx.core.database.getStringOrNull
import com.example.domain.entities.ContactEntity
import com.example.presentation.data.repositories.helpers.populators.DATA_FIELD_INDEX

class ContactPhonePopulator : ContactPopulator {
    override fun populate(contactEntity: ContactEntity, cursor: Cursor) =
        cursor.getStringOrNull(DATA_FIELD_INDEX)
            ?.let {
                contactEntity.copy(phones = contactEntity.phones.plus(it))
            }
            ?: contactEntity
}
