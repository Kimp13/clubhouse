package com.example.presentation.data.repositories.helpers.populators.contact

import android.database.Cursor
import com.example.domain.entities.ContactEntity

interface ContactPopulator {
    fun populate(contactEntity: ContactEntity, cursor: Cursor): ContactEntity
}
