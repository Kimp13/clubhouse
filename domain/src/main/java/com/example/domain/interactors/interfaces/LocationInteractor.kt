package com.example.domain.interactors.interfaces

import com.example.domain.entities.LocationEntity

private const val GEOCODING_DEFAULT_LANGUAGE = "en"

interface LocationInteractor {
    fun getLastLocation(onSuccess: (LocationEntity?) -> Unit)

    suspend fun reverseGeocode(
        location: LocationEntity,
        language: String = GEOCODING_DEFAULT_LANGUAGE
    ): String?
}