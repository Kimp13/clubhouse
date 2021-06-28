package com.example.presentation.data

import com.example.domain.entities.LocationEntity
import com.google.android.gms.maps.model.LatLng

fun LatLng.toLocationEntity() = LocationEntity(
    latitude,
    longitude
)

fun LocationEntity.toLatLng() = LatLng(
    latitude,
    longitude
)
