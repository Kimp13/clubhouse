package com.example.presentation.data

import com.example.domain.entities.ContactLocationEntity
import com.example.presentation.data.entities.ParcelableContactLocationEntity

fun ContactLocationEntity.toParcelable() = ParcelableContactLocationEntity(
    contactId,
    description,
    latitude,
    longitude
)