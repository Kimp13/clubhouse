package com.example.presentation.ui.interfaces

import com.example.domain.entities.ContactEntity
import com.example.presentation.data.entities.ParcelableSimpleContact

interface ContactLocationNavigator {
    fun navigateFrom(contact: ContactEntity)

    fun navigate(from: ParcelableSimpleContact, to: ParcelableSimpleContact)
}