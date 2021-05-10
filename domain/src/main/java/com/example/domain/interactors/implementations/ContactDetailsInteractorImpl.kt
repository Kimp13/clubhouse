package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import com.example.domain.repositories.ContactRepository
import com.example.domain.repositories.LocationRepository

class ContactDetailsInteractorImpl(
    private val contactRepository: ContactRepository,
    private val locationRepository: LocationRepository
) : ContactDetailsInteractor {
    override suspend fun getContact(lookup: String) =
        contactRepository.getContact(lookup)?.run {
            copy(
                location = locationRepository.findContactLocationById(
                    id
                )
            )
        }
}