package com.example.presentation.data

import com.example.domain.entities.ContactLocation
import com.example.domain.entities.LocationEntity
import com.google.android.gms.maps.model.LatLng

fun LatLng.toLocationEntity() = LocationEntity(
    latitude,
    longitude
)

fun ContactLocation.toLatLng() = LatLng(
    latitude,
    longitude
)
