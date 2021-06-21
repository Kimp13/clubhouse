package com.example.domain.interactors.implementations

import com.example.domain.entities.LocationEntity
import com.example.domain.interactors.interfaces.ContactLocationInteractor
import com.example.domain.interactors.interfaces.NavigatorInteractor
import com.example.domain.repositories.interfaces.LocationRepository

class ContactNavigatorInteractor(
    private val repository: LocationRepository
) : ContactLocationInteractor,
    NavigatorInteractor {
    override suspend fun findContactLocationById(id: Long) =
        repository.findContactLocationById(id)

    override suspend fun navigate(
        fromLocation: LocationEntity,
        toLocation: LocationEntity
    ) = repository.navigate(fromLocation, toLocation)
}
