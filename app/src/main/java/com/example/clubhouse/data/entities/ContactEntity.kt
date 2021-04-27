package com.example.clubhouse.data.entities

import android.os.Parcelable
import com.example.clubhouse.data.helpers.BirthDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactEntity(
    val id: Long,
    val lookup: String,
    val name: String?,
    val phoneNumbers: List<String>,
    val emails: List<String>,
    val description: String? = null,
    val birthDate: BirthDate? = null,
    val photoId: Long? = null
) : Parcelable