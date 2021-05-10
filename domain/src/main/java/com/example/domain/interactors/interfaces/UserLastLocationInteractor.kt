package com.example.domain.interactors.interfaces

import com.example.domain.entities.LocationEntity

interface UserLastLocationInteractor {
    fun getLastLocation(onSuccess: (LocationEntity?) -> Unit)
}