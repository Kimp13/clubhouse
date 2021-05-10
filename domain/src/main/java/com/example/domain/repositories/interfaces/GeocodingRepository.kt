package com.example.domain.repositories.interfaces

import com.example.domain.entities.LocationEntity

interface GeocodingRepository {
    suspend fun reverseGeocode(
        location: LocationEntity,
        language: String = "en"
    ): String?
}