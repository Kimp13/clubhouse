package com.example.presentation.data.repositories.helpers.populators.contact

import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.domain.entities.ContactEntity
import com.example.presentation.data.repositories.helpers.ContactProviderBirthDateHelper
import com.example.presentation.data.repositories.helpers.populators.DATA_ADDITIONAL_FIELD_INDEX
import com.example.presentation.data.repositories.helpers.populators.DATA_FIELD_INDEX

class ContactBirthdayPopulator : ContactPopulator {
    private val birthdayHelper = ContactProviderBirthDateHelper()

    override fun populate(contactEntity: ContactEntity, cursor: Cursor) =
        cursor.getIntOrNull(DATA_ADDITIONAL_FIELD_INDEX)
            ?.takeIf { eventType ->
                eventType == ContactsContract.CommonDataKinds
                    .Event
                    .TYPE_BIRTHDAY
            }
            ?.let { _ -> cursor.getStringOrNull(DATA_FIELD_INDEX) }
            ?.let { birthdayString ->
                contactEntity.copy(
                    birthDate = birthdayHelper.birthDateFromString(birthdayString)
                )
            }
            ?: contactEntity
}
