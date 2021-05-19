package com.example.presentation.data.repositories

import com.example.domain.entities.LocationEntity
import com.example.domain.repositories.NavigatorRepository
import com.example.presentation.BuildConfig
import com.example.presentation.data.apis.DirectionsApi
import com.example.presentation.data.toLocationEntity
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DirectionsRepository(
    private val directionsApi: DirectionsApi
) : NavigatorRepository {
    override suspend fun navigate(
        fromLocation: LocationEntity,
        toLocation: LocationEntity
    ) = withContext(Dispatchers.IO) {
        directionsApi.navigate(
            "${fromLocation.latitude},${fromLocation.longitude}",
            "${toLocation.latitude},${toLocation.longitude}",
            BuildConfig.MAPS_API_KEY
        )
            .routes
            .firstOrNull()
            ?.legs
            ?.firstOrNull()
            ?.steps
            ?.fold(emptyList<LocationEntity>()) { result, step ->
                result.plus(PolyUtil.decode(step.polyline.points).map {
                    it.toLocationEntity()
                })
            }
    }
}