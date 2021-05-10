package com.example.presentation.data.entities

import com.example.domain.entities.ContactLocation
import com.google.android.gms.maps.model.LatLng

data class LocationWithDescription(
    val latitude: Double,
    val longitude: Double,
    val description: String
)

fun ContactLocation.withDescription(description: String) = LocationWithDescription(
    latitude,
    longitude,
    description
)

fun LocationWithDescription.toLatLng() = LatLng(
    latitude,
    longitude
)
