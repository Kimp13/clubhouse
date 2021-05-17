package com.example.domain.repositories

import com.example.domain.entities.LocationEntity

interface GeocodingRepository {
    suspend fun reverseGeocode(
        location: LocationEntity,
        language: String = "en"
    ): String?
}