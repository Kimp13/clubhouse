package com.example.presentation.ui.interfaces

import com.example.domain.entities.ContactEntity
import com.example.presentation.data.entities.ParcelableSimpleContact

interface FragmentStackGateway {
    fun onCardClick(lookup: String)

    fun navigateFrom(contact: ContactEntity)

    fun navigate(from: ParcelableSimpleContact, to: ParcelableSimpleContact)

    fun retrieveContactLocation(contact: ContactEntity)

    fun viewContactLocation(contactEntity: ContactEntity)

    fun viewAllContactsLocation()

    fun pleadForReadContactsPermission()

    fun popBackStack()
}
