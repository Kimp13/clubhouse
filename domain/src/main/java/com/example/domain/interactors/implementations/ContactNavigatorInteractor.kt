package com.example.domain.interactors.implementations

import com.example.domain.entities.LocationEntity
import com.example.domain.interactors.interfaces.ContactLocationInteractor
import com.example.domain.interactors.interfaces.NavigatorInteractor
import com.example.domain.repositories.LocationRepository
import com.example.domain.repositories.NavigatorRepository

class ContactNavigatorInteractor(
    private val locationRepository: LocationRepository,
    private val navigatorRepository: NavigatorRepository
) : ContactLocationInteractor,
    NavigatorInteractor {
    override suspend fun findContactLocationById(id: Long) =
        locationRepository.findContactLocationById(id)

    override suspend fun navigate(
        fromLocation: LocationEntity,
        toLocation: LocationEntity
    ) = navigatorRepository.navigate(fromLocation, toLocation)
}