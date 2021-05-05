package com.example.domain.entities

data class ContactLocationEntity(
    val contactId: Long,
    val description: String?,
    val latitude: Double,
    val longitude: Double
)