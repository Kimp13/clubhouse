package com.example.domain.repositories

import com.example.domain.entities.LocationEntity

interface NavigatorRepository {
    suspend fun navigate(
        fromLocation: LocationEntity,
        toLocation: LocationEntity
    ): List<LocationEntity>?
}