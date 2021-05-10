package com.example.domain.entities

data class ContactLocation(
    val contactId: Long,
    val description: String?,
    val latitude: Double,
    val longitude: Double
)

fun ContactLocation.toLocationEntity() = LocationEntity(
    latitude,
    longitude
)