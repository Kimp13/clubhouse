package com.example.domain.repositories.interfaces

import com.example.domain.entities.ContactLocation
import com.example.domain.entities.LocationEntity

interface LocationRepository {
    suspend fun findContactLocationById(id: Long): ContactLocation?

    suspend fun addContactLocation(location: ContactLocation)

    suspend fun getAll(): List<ContactLocation>

    suspend fun navigate(
        fromLocation: LocationEntity,
        toLocation: LocationEntity
    ): List<LocationEntity>?

    fun getUserLastLocation(onSuccess: (LocationEntity?) -> Unit)
}