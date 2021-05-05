package com.example.domain.repositories

import com.example.domain.entities.LocationEntity

interface LastLocationRepository {
    fun getLastLocation(onSuccess: (LocationEntity?) -> Unit)
}