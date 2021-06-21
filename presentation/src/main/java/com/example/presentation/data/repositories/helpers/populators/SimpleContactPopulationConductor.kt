package com.example.presentation.data.repositories.helpers.populators

import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull
import com.example.presentation.data.repositories.helpers.populators.simplecontact.NullSimpleContactPopulator
import com.example.presentation.data.repositories.helpers.populators.simplecontact.SimpleContactPhonePopulator
import com.example.presentation.data.repositories.helpers.populators.simplecontact.SimpleContactPhotoPopulator

class SimpleContactPopulationConductor {
    private val nullPopulator = NullSimpleContactPopulator()
    private val map = mapOf(
        ContactsContract.CommonDataKinds
            .Phone
            .CONTENT_ITEM_TYPE to SimpleContactPhonePopulator(),

        ContactsContract.CommonDataKinds
            .Photo
            .CONTENT_ITEM_TYPE to SimpleContactPhotoPopulator()
    )

    fun populatorFromCursor(cursor: Cursor) = map.get(
        cursor.getStringOrNull(DATA_MIMETYPE_INDEX)
    ) ?: nullPopulator
}
