package com.example.presentation.data

import com.google.android.gms.maps.model.LatLng

fun LatLng.toLocationEntity() = com.example.domain.entities.LocationEntity(
    latitude,
    longitude
)