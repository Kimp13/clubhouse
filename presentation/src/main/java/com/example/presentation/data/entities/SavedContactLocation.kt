package com.example.presentation.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.entities.ContactLocation

@Entity(tableName = "contact_location")
data class SavedContactLocation(
    @PrimaryKey
    @ColumnInfo(name = "contact_id") val contactId: Long,
    val description: String?,
    val latitude: Double,
    val longitude: Double
)

fun ContactLocation.toDatabaseEntity() = SavedContactLocation(
    contactId,
    description,
    latitude,
    longitude
)

fun SavedContactLocation.toDomainEntity() = ContactLocation(
    contactId,
    description,
    latitude,
    longitude
)
