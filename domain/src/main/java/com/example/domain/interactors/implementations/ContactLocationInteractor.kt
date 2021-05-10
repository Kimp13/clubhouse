package com.example.domain.interactors.implementations

import com.example.domain.entities.ContactLocation
import com.example.domain.entities.LocationEntity
import com.example.domain.interactors.interfaces.ContactLocationStorageInteractor
import com.example.domain.interactors.interfaces.LocationInteractor
import com.example.domain.interactors.interfaces.MapControlsClarificationInteractor
import com.example.domain.interactors.interfaces.UserLastLocationInteractor
import com.example.domain.repositories.BasicTypesRepository
import com.example.domain.repositories.LocationRepository
import com.example.domain.repositories.LastLocationRepository
import com.example.domain.repositories.SharedPreferencesRepository
import com.example.domain.repositories.GeocodingRepository
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.LastLocationRepository

private const val ARE_MAP_CONTROLS_CLARIFIED_KEY = "are_map_controls_clarified?"

class ContactLocationInteractor(
    private val locationRepository: LocationRepository,
    private val basicTypesRepository: BasicTypesRepository,
    private val geocodingRepository: GeocodingRepository
) : LocationInteractor,
    MapControlsClarificationInteractor {
    override fun getLastLocation(onSuccess: (LocationEntity?) -> Unit) {
        locationRepository.getLastLocation(onSuccess)
    }

    override suspend fun areMapControlsClarified() =
        basicTypesRepository.getBoolean(
            ARE_MAP_CONTROLS_CLARIFIED_KEY
        )

    override suspend fun writeMapControlsClarified() =
        basicTypesRepository.writeBoolean(
            ARE_MAP_CONTROLS_CLARIFIED_KEY,
            true
        )

    override suspend fun reverseGeocode(
        location: LocationEntity,
        language: String
    ) = geocodingRepository.reverseGeocode(location, language)

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