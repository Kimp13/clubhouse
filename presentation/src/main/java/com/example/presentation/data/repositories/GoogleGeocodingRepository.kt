package com.example.presentation.data.repositories

import com.example.domain.entities.LocationEntity
import com.example.domain.repositories.interfaces.GeocodingRepository
import com.example.presentation.BuildConfig
import com.example.presentation.data.apis.GeocodingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleGeocodingRepository(
    private val api: GeocodingApi
) : GeocodingRepository {
    override suspend fun reverseGeocode(
        location: LocationEntity,
        language: String
    ) = withContext(Dispatchers.IO) {
        api.reverseGeocode(
            "${location.latitude},${location.longitude}",
            BuildConfig.MAPS_API_KEY,
            language
        )
            .results
            .firstOrNull()
            ?.formattedAddress
    }
}