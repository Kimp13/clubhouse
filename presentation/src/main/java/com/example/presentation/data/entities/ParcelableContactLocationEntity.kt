package com.example.presentation.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableContactLocationEntity(
    val contactId: Long,
    val description: String?,
    val latitude: Double,
    val longitude: Double
) : Parcelable