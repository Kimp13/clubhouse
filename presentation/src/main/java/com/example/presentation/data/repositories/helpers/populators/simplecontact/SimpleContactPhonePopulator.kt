package com.example.presentation.data.repositories.helpers.populators.simplecontact

import android.database.Cursor
import androidx.core.database.getStringOrNull
import com.example.domain.entities.SimpleContactEntity
import com.example.presentation.data.repositories.helpers.populators.DATA_FIELD_INDEX

class SimpleContactPhonePopulator : SimpleContactPopulator {
    override fun populate(
        simpleContactEntity: SimpleContactEntity,
        cursor: Cursor
    ) = cursor.getStringOrNull(DATA_FIELD_INDEX)
        ?.let { phone ->
            simpleContactEntity.copy(
                phoneNumber = phone
            )
        }
        ?: simpleContactEntity
}
