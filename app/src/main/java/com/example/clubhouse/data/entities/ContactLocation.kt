package com.example.clubhouse.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contact_location")
data class ContactLocation(
    @PrimaryKey
    @ColumnInfo(name = "contact_id") val contactId: Long,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
) : Parcelable