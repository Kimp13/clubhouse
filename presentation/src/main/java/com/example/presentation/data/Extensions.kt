package com.example.presentation.data

import com.example.domain.entities.ContactLocationEntity
import com.example.domain.entities.LocationEntity
import com.example.presentation.data.entities.ParcelableContactLocationEntity
import com.google.android.gms.maps.model.LatLng

fun LatLng.toLocationEntity() = LocationEntity(
    latitude,
    longitude
)

fun ContactLocationEntity.toParcelable() = ParcelableContactLocationEntity(
    contactId,
    description,
    latitude,
    longitude
)