package com.example.domain.interactors.interfaces

import com.example.domain.entities.LocationEntity

interface NavigatorInteractor {
    suspend fun navigate(
        fromLocation: LocationEntity,
        toLocation: LocationEntity
    ): List<LocationEntity>?
}
