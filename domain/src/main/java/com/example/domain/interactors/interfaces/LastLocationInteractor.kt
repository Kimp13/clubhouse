package com.example.domain.interactors.interfaces

import com.example.domain.entities.LocationEntity

interface LastLocationInteractor {
    fun getLastLocation(onSuccess: (LocationEntity?) -> Unit)
}