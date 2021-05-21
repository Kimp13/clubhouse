package com.example.presentation.data.entities

import android.os.Parcelable
import com.example.domain.entities.ContactLocation
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableContactLocation(
    val contactId: Long,
    val description: String?,
    val latitude: Double,
    val longitude: Double
) : Parcelable

fun ContactLocation.toParcelable() = ParcelableContactLocation(
    contactId,
    description,
    latitude,
    longitude
)
