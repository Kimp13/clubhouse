package com.example.domain.interactors.implementations

import com.example.domain.entities.ContactLocation
import com.example.domain.entities.LocationEntity
import com.example.domain.interactors.interfaces.ContactLocationStorageInteractor
import com.example.domain.interactors.interfaces.MapControlsClarificationInteractor
import com.example.domain.interactors.interfaces.UserLastLocationInteractor
import com.example.domain.repositories.BasicTypesRepository
import com.example.domain.repositories.LocationRepository

private const val ARE_MAP_CONTROLS_CLARIFIED_KEY = "are_map_controls_clarified?"

class ContactLocationInteractor(
    private val locationRepository: LocationRepository,
    private val sharedPreferencesRepository: BasicTypesRepository
) : UserLastLocationInteractor,
    MapControlsClarificationInteractor,
    ContactLocationStorageInteractor {
    override fun getLastLocation(onSuccess: (LocationEntity?) -> Unit) {
        locationRepository.getUserLastLocation(onSuccess)
    }

    override suspend fun areMapControlsClarified() =
        sharedPreferencesRepository.getBoolean(
            ARE_MAP_CONTROLS_CLARIFIED_KEY
        )

    override suspend fun writeMapControlsClarified() =
        sharedPreferencesRepository.writeBoolean(
            ARE_MAP_CONTROLS_CLARIFIED_KEY,
            true
        )

    override suspend fun findContactLocationById(
        id: Long
    ): ContactLocation? {
        return locationRepository.findContactLocationById(id)
    }

    override suspend fun addContactLocation(
        locationEntity: ContactLocation
    ) {
        locationRepository.addContactLocation(locationEntity)
    }
}