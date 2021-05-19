package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.SimpleContactListInteractor
import com.example.domain.repositories.ContactRepository
import com.example.domain.repositories.LocationRepository

class ContactListFragmentInteractor(
    private val contactRepository: ContactRepository,
    private val locationRepository: LocationRepository
) : SimpleContactListInteractor {
    override suspend fun getSimpleContacts(
        query: String?,
        excludedContactId: Long?
    ) = contactRepository.getSimpleContacts(
        query,
        excludedContactId?.let { excludedId ->
            locationRepository.getAll()
                .map {
                    it.contactId
                }
                .filterNot {
                    it == excludedId
                }
        }
    )
}