package com.example.domain.interactors.implementations

import com.example.domain.entities.LocationEntity
import com.example.domain.interactors.interfaces.LocationInteractor
import com.example.domain.interactors.interfaces.MapControlsClarificationInteractor
import com.example.domain.repositories.GeocodingRepository
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.LastLocationRepository

private const val ARE_MAP_CONTROLS_CLARIFIED_KEY = "are_map_controls_clarified?"

class ContactLocationInteractor(
    private val locationRepository: LastLocationRepository,
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
}