package com.example.domain.repositories

import com.example.domain.entities.ContactLocation
import com.example.domain.entities.LocationEntity

interface LocationRepository {
    suspend fun findContactLocationById(id: Long): ContactLocation?

    suspend fun addContactLocation(location: ContactLocation)

    fun getUserLastLocation(onSuccess: (LocationEntity?) -> Unit)
}