package com.example.presentation.data.entities

import android.os.Parcelable
import com.example.domain.entities.ContactEntity
import com.example.domain.entities.SimpleContactEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableSimpleContact(
    val id: Long,
    val lookup: String,
    val name: String? = null,
    val phoneNumber: String? = null,
    val photoId: Long? = null
) : Parcelable

fun SimpleContactEntity.toParcelable() = ParcelableSimpleContact(
    id,
    lookup,
    name,
    phoneNumber,
    photoId
)

fun ContactEntity.toSimpleParcelable() = ParcelableSimpleContact(
    id,
    lookup,
    name,
    phones.firstOrNull(),
    photoId
)
