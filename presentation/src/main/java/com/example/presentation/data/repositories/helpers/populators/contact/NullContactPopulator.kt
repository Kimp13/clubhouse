package com.example.presentation.data.repositories.helpers.populators.contact

import android.database.Cursor
import com.example.domain.entities.ContactEntity

class NullContactPopulator : ContactPopulator {
    override fun populate(contactEntity: ContactEntity, cursor: Cursor): ContactEntity {
        return contactEntity
    }
}
