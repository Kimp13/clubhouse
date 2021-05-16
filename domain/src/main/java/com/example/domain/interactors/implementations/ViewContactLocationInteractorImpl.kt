package com.example.domain.interactors.implementations

import com.example.domain.interactors.interfaces.ViewContactLocationInteractor
import com.example.domain.repositories.ContactRepository
import com.example.domain.repositories.LocationRepository

class ViewContactLocationInteractorImpl(
    private val contactRepository: ContactRepository,
    private val locationRepository: LocationRepository
) : ViewContactLocationInteractor {
    override suspend fun findContactById(id: Long) =
        contactRepository.findContactById(id)?.copy(
            location = locationRepository.findContactLocationById(id)
        )

    override suspend fun getAllContactsWithLocation() =
        locationRepository.getAll()
            .map {
                it.contactId to it
            }
            .toMap()
            .let { idToLocation ->
                contactRepository.findContactsById(idToLocation.keys.toList())
                    .map {
                        it.copy(
                            location = idToLocation[it.id]
                        )
                    }
            }
}