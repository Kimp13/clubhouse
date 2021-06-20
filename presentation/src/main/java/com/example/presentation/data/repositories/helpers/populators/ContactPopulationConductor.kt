package com.example.presentation.data.repositories.helpers.populators

import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull
import com.example.presentation.data.repositories.helpers.populators.contact.ContactBirthdayPopulator
import com.example.presentation.data.repositories.helpers.populators.contact.ContactDescriptionPopulator
import com.example.presentation.data.repositories.helpers.populators.contact.ContactEmailPopulator
import com.example.presentation.data.repositories.helpers.populators.contact.ContactNamePopulator
import com.example.presentation.data.repositories.helpers.populators.contact.ContactPhonePopulator
import com.example.presentation.data.repositories.helpers.populators.contact.ContactPhotoPopulator
import com.example.presentation.data.repositories.helpers.populators.contact.NullContactPopulator

const val DATA_MIMETYPE_INDEX = 0
const val DATA_LOOKUP_INDEX = 1
const val DATA_FIELD_INDEX = 2
const val DATA_ADDITIONAL_FIELD_INDEX = 3
const val DATA_PHOTO_ID_INDEX = 4

class ContactPopulationConductor {
    private val nullPopulator = NullContactPopulator()
    private val map = mapOf(
        ContactsContract.CommonDataKinds
            .StructuredName
            .CONTENT_ITEM_TYPE to ContactNamePopulator(),

        ContactsContract.CommonDataKinds
            .Email
            .CONTENT_ITEM_TYPE to ContactEmailPopulator(),

        ContactsContract.CommonDataKinds
            .Phone
            .CONTENT_ITEM_TYPE to ContactPhonePopulator(),

        ContactsContract.CommonDataKinds
            .Note
            .CONTENT_ITEM_TYPE to ContactDescriptionPopulator(),

        ContactsContract.CommonDataKinds
            .Event
            .CONTENT_ITEM_TYPE to ContactBirthdayPopulator(),

        ContactsContract.CommonDataKinds
            .Photo
            .CONTENT_ITEM_TYPE to ContactPhotoPopulator()
    )

    fun populatorFromCursor(cursor: Cursor) = map.get(
        cursor.getStringOrNull(DATA_MIMETYPE_INDEX)
    ) ?: nullPopulator
}
